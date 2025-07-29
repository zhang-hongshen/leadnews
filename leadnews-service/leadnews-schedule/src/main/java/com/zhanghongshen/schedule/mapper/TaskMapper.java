package com.zhanghongshen.schedule.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhanghongshen.model.schedule.pojo.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    List<Task> listExecuteTimeBeforeByTypeAndPriority(@Param("taskType") int type,
                               @Param("priority") int priority,
                               @Param("date") Date date);
}
