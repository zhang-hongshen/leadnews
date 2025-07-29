package com.zhanghongshen.schedule.feign;

import com.zhanghongshen.apis.schedule.ScheduleClient;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.schedule.dto.TaskDto;
import com.zhanghongshen.schedule.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/task")
@RequiredArgsConstructor
public class ScheduleClientImpl  implements ScheduleClient {

    private final TaskService taskService;

    /**
     * 添加任务
     * @param dto 任务对象
     * @return 任务id
     */
    @PostMapping("/add")
    @Override
    public ResponseResult addTask(@RequestBody TaskDto dto) {
        return ResponseResult.success(taskService.addTask(dto));
    }

    /**
     *
     * @param taskId
     * @return
     */
    @GetMapping("/cancel/{taskId}")
    @Override
    public ResponseResult cancelTask(@PathVariable("taskId") long taskId) {
        taskService.cancelTask(taskId);
        return ResponseResult.success();
    }

    /**
     *
     * @return
     */
    @GetMapping("/poll")
    @Override
    public ResponseResult<TaskDto> poll(@RequestBody TaskDto dto) {
        return ResponseResult.success(taskService.poll(dto));
    }
}
