package com.zhanghongshen.model.schedule.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TaskDto {

    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 类型
     */
    private Integer taskType;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 执行id
     */
    private Date executeTime;

    /**
     * task参数
     */
    private byte[] parameters;

}
