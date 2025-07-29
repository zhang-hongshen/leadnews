package com.zhanghongshen.schedule.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.constants.ScheduleConstants;
import com.zhanghongshen.common.lock.DistributedLockTemplate;
import com.zhanghongshen.model.schedule.dto.TaskDto;
import com.zhanghongshen.model.schedule.pojo.Task;
import com.zhanghongshen.model.schedule.pojo.TaskLog;
import com.zhanghongshen.schedule.mapper.TaskLogMapper;
import com.zhanghongshen.schedule.mapper.TaskMapper;
import com.zhanghongshen.schedule.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.StringRedisConnection;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    private final TaskLogMapper taskLogMapper;

    private final CacheService cacheService;

    private final TransactionTemplate transactionTemplate;

    private final DistributedLockTemplate distributedLockTemplate;

    /**
     * 添加延迟任务
     *
     * @param dto
     * @return
     */
    @Override
    public long addTask(TaskDto dto) {

        addTaskToDb(dto);
        addTaskToCache(dto);

        return dto.getTaskId();
    }

    private String cacheKey(int taskType, int priority) {
        return taskType + "_" + priority;
    }

    private void addTaskToDb(TaskDto dto) {
        Task task = new Task();
        BeanUtils.copyProperties(dto, task);
        task.setExecuteTime(dto.getExecuteTime());

        transactionTemplate.execute(status -> {
            taskMapper.insert(task);

            dto.setTaskId(task.getId());

            TaskLog taskLog = new TaskLog();
            BeanUtils.copyProperties(dto, taskLog);
            taskLog.setVersion(1);
            taskLog.setStatus(ScheduleConstants.SCHEDULED);
            taskLogMapper.insert(taskLog);
            return null;
        });
    }

    /**
     * 把任务添加到redis中
     *
     * @param dto
     */
    private void addTaskToCache(TaskDto dto) {

        String key = cacheKey(dto.getTaskType(), dto.getPriority());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        Date nextScheduleDate = calendar.getTime();

        if (dto.getExecuteTime().before(calendar.getTime())) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(dto));
        } else if (dto.getExecuteTime().before(nextScheduleDate)) {
            cacheService.zAdd(ScheduleConstants.FUTURE + key, JSON.toJSONString(dto), dto.getExecuteTime().getTime());
        }
    }



    @Override
    public void cancelTask(long taskId) {

        TaskDto taskDto = updateTaskStatus(taskId, ScheduleConstants.CANCELLED);
        removeTaskFromCache(taskDto);
    }

    private TaskDto updateTaskStatus(long taskId, int status) {
        TaskLog entity = transactionTemplate.execute(transactionStatus -> {
            taskMapper.deleteById(taskId);
            TaskLog taskLog = taskLogMapper.selectById(taskId);
            taskLog.setStatus(status);
            taskLogMapper.updateById(taskLog);
            return taskLog;
        });
        TaskDto task = new TaskDto();
        BeanUtils.copyProperties(entity, task);
        task.setExecuteTime(entity.getExecuteTime());
        return task;
    }

    /**
     * 删除redis中的任务数据
     * @param taskDto
     */
    private void removeTaskFromCache(TaskDto taskDto) {

        String key = cacheKey(taskDto.getTaskType(), taskDto.getPriority());

        if(taskDto.getExecuteTime().getTime() <= System.currentTimeMillis()){
            cacheService.lRemove(ScheduleConstants.TOPIC + key,0, JSON.toJSONString(taskDto));
        } else {
            cacheService.zRemove(ScheduleConstants.FUTURE + key, JSON.toJSONString(taskDto));
        }
    }

    @Override
    public TaskDto poll(TaskDto dto) {
        String key = cacheKey(dto.getTaskType(), dto.getPriority());
        String taskJson = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
        if(StringUtils.isBlank(taskJson)){
            return null;
        }
        TaskDto taskDto = JSON.parseObject(taskJson, TaskDto.class);
        updateTaskStatus(taskDto.getTaskId(), ScheduleConstants.EXECUTED);
        return taskDto;
    }

    @Scheduled(cron = "0 */1 * * * ?")
    @PostConstruct
    private void addFutureTaskToCurrentTaskList() {
        distributedLockTemplate.executeWithoutResult("FUTURE_TASK_SYNC_LOCK", 30, TimeUnit.SECONDS, () -> {
            Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");// future_*
            for (String futureKey : futureKeys) {
                String topicKey = ScheduleConstants.TOPIC + futureKey.split(ScheduleConstants.FUTURE)[1];

                Set<String> tasks = cacheService.zRangeByScore(futureKey, 0, System.currentTimeMillis());
                if (tasks.isEmpty()) {
                    continue;
                }
                String[] strings = tasks.toArray(new String[0]);
                cacheService.executePipelined(connection -> {
                    StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
                    stringRedisConnection.rPush(topicKey, strings);
                    stringRedisConnection.zRem(futureKey, strings);
                    return null;
                });
            }
        });
    }

    @Scheduled(cron = "0 */5 * * * ?")
    @PostConstruct
    private void addTaskFromDbToCache() {
        clearCache();
        log.info("sync database task to cache");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);

        List<Task> tasks = taskMapper.selectList(Wrappers.<Task>lambdaQuery()
                .lt(Task::getExecuteTime, calendar.getTime()));
        if(tasks.isEmpty()) {
            return;
        }
        for (Task task : tasks) {
            TaskDto taskDto = new TaskDto();
            BeanUtils.copyProperties(task, taskDto);
            taskDto.setExecuteTime(task.getExecuteTime());
            addTaskToCache(taskDto);
        }
    }

    private void clearCache(){
        Set<String> futureKeys = cacheService.scan(ScheduleConstants.FUTURE + "*");
        Set<String> topicKeys = cacheService.scan(ScheduleConstants.TOPIC + "*");
        cacheService.delete(topicKeys);
        cacheService.delete(futureKeys);
//        cacheService.executePipelined(connection -> {
//            StringRedisConnection stringRedisConnection = (StringRedisConnection) connection;
//            stringRedisConnection.del(topicKeys.toArray(new String[0]));
//            stringRedisConnection.del(futureKeys.toArray(new String[0]));
//            return null;
//        });
    }
}