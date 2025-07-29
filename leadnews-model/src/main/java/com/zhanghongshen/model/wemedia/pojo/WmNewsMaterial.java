package com.zhanghongshen.model.wemedia.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhanghongshen.model.common.BaseEntity;
import lombok.Data;

@Data
@TableName("wm_news_material")
public class WmNewsMaterial extends BaseEntity {


    /**
     * 素材ID
     */
    @TableField("material_id")
    private Long materialId;

    /**
     * 图文ID
     */
    @TableField("news_id")
    private Long newsId;

    /**
     * 引用类型
     0 内容引用
     1 主图引用
     */
    @TableField("type")
    private Short type;

    /**
     * 引用排序
     */
    @TableField("ord")
    private Short ord;

}
