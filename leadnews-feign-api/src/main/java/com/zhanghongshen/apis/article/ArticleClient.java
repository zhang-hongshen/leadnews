package com.zhanghongshen.apis.article;

import com.zhanghongshen.model.article.dto.ArticleDto;
import com.zhanghongshen.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "leadnews-article")
public interface ArticleClient {

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto dto);


    @DeleteMapping("/api/v1/article/{articleId}")
    ResponseResult deleteArticle(@PathVariable("articleId") Long articleId);
}