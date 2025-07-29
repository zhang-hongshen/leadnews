package com.zhanghongshen.model.article.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleHomeDto {

    // 最大时间
    Date maxBehotTime;
    // 最小时间
    Date minBehotTime;

    Integer size;

    String tag;

    private final static int  MAX_PAGE_SIZE = 50;

    public void checkParam() {
        if(size == null || size == 0){
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
    }
}
