package com.zhanghongshen.model.common;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.zhanghongshen.common.annotation.StringNumberAdapter;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;


@Data
public class BaseEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @StringNumberAdapter
    private Long id;

    @TableField(value = "create_time")
    private Date createTime;

    @TableField(value = "update_time")
    private Date updateTime;
}
