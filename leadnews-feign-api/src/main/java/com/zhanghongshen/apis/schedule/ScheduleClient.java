package com.zhanghongshen.apis.schedule;

import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.schedule.dto.TaskDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-schedule")
public interface ScheduleClient {

    /**
     * 添加任务
     * @param dto   任务对象
     * @return      任务id
     */
    @PostMapping("/api/v1/task/add")
    ResponseResult addTask(@RequestBody TaskDto dto);

    /**
     * 取消任务
     * @param taskId        任务id
     * @return              取消结果
     */
    @GetMapping("/api/v1/task/cancel/{taskId}")
    ResponseResult cancelTask(@PathVariable("taskId") long taskId);

    /**
     * 按照类型和优先级来拉取任务
     * @return
     */
    @GetMapping("/api/v1/task/poll")
    ResponseResult<TaskDto> poll(@RequestBody TaskDto dto);
}