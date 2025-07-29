package com.zhanghongshen.model.wemedia.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("wm_sensitive")
public class WmSensitive {

    /**
     * 敏感词
     */
    @TableField("sensitives")
    private String sensitives;

}
