package com.zhanghongshen.model.article.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zhanghongshen.common.annotation.StringNumberAdapter;
import com.zhanghongshen.common.constants.ArticleConstants;
import com.zhanghongshen.model.common.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ap_article")
public class Article extends BaseEntity {

    private String title;

    @TableField("author_id")
    @StringNumberAdapter
    private Long authorId;

    @TableField("author_name")
    private String authorName;

    @TableField("channel_id")
    private Long channelId;

    @TableField("channel_name")
    private String channelName;

    /**
     * 文章布局  0 无图文章   1 单图文章    2 多图文章
     */
    private Short layout;

    /**
     * 文章标记  0 普通文章   1 热点文章   2 置顶文章   3 精品文章   4 大V 文章
     */
    private Byte flag;

    /**
     * 文章封面图片 多张逗号分隔
     */
    private String images;

    private String labels;

    private Integer likes;

    private Integer collection;

    private Integer comment;

    private Integer views;

    @TableField("province_id")
    private Integer provinceId;

    @TableField("city_id")
    private Integer cityId;

    @TableField("county_id")
    private Integer countyId;

    @TableField("publish_time")
    private Date publishTime;

    @TableField("sync_status")
    private Boolean syncStatus;

    private Boolean origin;

    @TableField("static_url")
    private String staticUrl;

    public int getScore() {
        int score = 0;
        if(getLikes() != null){
            score += getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
        }
        if(getViews() != null){
            score += getViews();
        }
        if(getComment() != null){
            score += getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
        }
        if(getCollection() != null){
            score += getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
        }
        return score;
    }
}
