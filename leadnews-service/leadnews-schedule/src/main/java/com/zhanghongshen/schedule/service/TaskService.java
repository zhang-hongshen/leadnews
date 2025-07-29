package com.zhanghongshen.schedule.service;

import com.zhanghongshen.model.schedule.dto.TaskDto;

public interface TaskService {

    /**
     * 添加任务
     * @param dto   任务对象
     * @return       任务id
     */
    long addTask(TaskDto dto) ;

    void cancelTask(long taskId);

    TaskDto poll(TaskDto dto);
}
