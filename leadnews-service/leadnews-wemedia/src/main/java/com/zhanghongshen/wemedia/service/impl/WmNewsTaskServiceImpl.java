package com.zhanghongshen.wemedia.service.impl;

import com.zhanghongshen.apis.schedule.ScheduleClient;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.common.enums.TaskTypeEnum;
import com.zhanghongshen.model.schedule.dto.TaskDto;
import com.zhanghongshen.model.wemedia.pojo.WmNews;
import com.zhanghongshen.utils.ProtostuffUtils;
import com.zhanghongshen.wemedia.service.WmNewsAutoScanService;
import com.zhanghongshen.wemedia.service.WmNewsTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.config.Task;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class WmNewsTaskServiceImpl  implements WmNewsTaskService {

    private final ScheduleClient scheduleClient;

    private final WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 添加任务到延迟队列中
     * @param newsId          文章的id
     * @param publishTime 发布的时间  可以做为任务的执行时间
     */
    @Override
    @Async
    public void addNewsToTask(Long newsId, Date publishTime) {

        log.info("[addNewsToTask] begin");

        TaskDto task = new TaskDto();
        task.setExecuteTime(publishTime);
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());

        WmNews wmNews = new WmNews();
        wmNews.setId(newsId);

        task.setParameters(ProtostuffUtils.serialize(wmNews));

        scheduleClient.addTask(task);

        log.info("[addNewsToTask] end");

    }

    @Scheduled(fixedRate = 1000)
    @Override
    public void scanNewsByTask() {
        TaskDto task = new TaskDto();
        task.setTaskType(TaskTypeEnum.NEWS_SCAN_TIME.getTaskType());
        task.setPriority(TaskTypeEnum.NEWS_SCAN_TIME.getPriority());
        ResponseResult<TaskDto> responseResult = scheduleClient.poll(task);
        if(responseResult == null || !responseResult.isSuccess()) {
            log.error("[scanNewsByTask] scheduleClient.getTask failed.");
            return;
        }
        if(responseResult.getData() == null) {
            return;
        }
        task = responseResult.getData();
        byte[] parameters = task.getParameters();
        WmNews wmNews = ProtostuffUtils.deserialize(parameters, WmNews.class);
        wmNewsAutoScanService.autoScanWmNews(wmNews.getId());
    }
}
