package com.zhanghongshen.model.article.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zhanghongshen.common.annotation.StringNumberAdapter;
import com.zhanghongshen.model.article.pojo.Article;
import lombok.Data;

import java.util.Date;

@Data
public class ArticleDto {

    private String title;

    @StringNumberAdapter
    private Long authorId;

    private String authorName;

    private Long channelId;

    private String channelName;

    private Short layout;

    private Byte flag;

    private String images;

    private String labels;

    private Integer likes;

    private Integer collection;

    private Integer comment;

    private Integer views;

    private Integer provinceId;

    private Integer cityId;

    private Integer countyId;

    private Date publishTime;

    private Boolean syncStatus;

    private Boolean origin;

    private String content;

    private Long articleId;
}
