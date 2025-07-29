package com.zhanghongshen.model.article.dto;

import lombok.Data;

@Data
public class ArticleBehaviorDto {

    private Long articleId;

    private int view;

    private int collect;

    private int comment;

    private int like;
}