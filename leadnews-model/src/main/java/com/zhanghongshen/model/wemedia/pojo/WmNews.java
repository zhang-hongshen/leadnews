package com.zhanghongshen.model.wemedia.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zhanghongshen.model.common.BaseEntity;
import lombok.Data;
import lombok.Getter;
import org.apache.ibatis.type.Alias;

import java.util.Date;

@Data
@TableName("wm_news")
public class WmNews extends BaseEntity {

    /**
     * 自媒体用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 图文内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章布局
     0 无图文章
     1 单图文章
     3 多图文章
     */
    @TableField("type")
    private Short type;

    /**
     * 图文频道ID
     */
    @TableField("channel_id")
    private Long channelId;

    @TableField("labels")
    private String labels;

    /**
     * 提交时间
     */
    @TableField("submit_time")
    @JsonProperty("submittedTime")
    private Date submitTime;

    /**
     * 当前状态
     0 草稿
     1 提交（待审核）
     2 审核失败
     3 人工审核
     4 人工审核通过
     8 审核通过（待发布）
     9 已发布
     */
    @TableField("status")
    private Short status;

    /**
     * 定时发布时间，不定时则为空
     */
    @TableField("publish_time")
    private Date publishTime;

    /**
     * 拒绝理由
     */
    @TableField("reason")
    private String reason;

    /**
     * 发布库文章ID
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * //图片用逗号分隔
     */
    @TableField("images")
    private String images;

    @TableField("enable")
    private Short enable;

    //状态枚举类
    @Alias("WmNewsStatus")
    @Getter
    public enum Status{
        NORMAL((short)0),
        SUBMIT((short)1),
        FAIL((short)2),
        ADMIN_AUTH((short)3),
        ADMIN_SUCCESS((short)4),
        SUCCESS((short)8),
        PUBLISHED((short)9);
        final short code;

        Status(short code){
            this.code = code;
        }

    }

}
