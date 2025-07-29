package com.zhanghongshen.article.controller;


import com.zhanghongshen.article.service.ArticleService;
import com.zhanghongshen.article.annotation.RateLimit;
import com.zhanghongshen.common.constants.ArticleConstants;
import com.zhanghongshen.model.article.dto.ArticleHomeDto;
import com.zhanghongshen.model.article.pojo.Article;
import com.zhanghongshen.common.dto.ResponseResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/article")
@RequiredArgsConstructor
public class ArticleHomeController {

    private final ArticleService articleService;

    @PostMapping("/load")
    public ResponseResult<List<Article>> load(@RequestBody ArticleHomeDto dto){
        return articleService.listArticle(dto, ArticleConstants.LOADTYPE_LOAD_MORE, true);
    }

    @PostMapping("/loadmore")
    public ResponseResult<List<Article>> loadMore(@RequestBody ArticleHomeDto dto){
        return articleService.listArticle(dto, ArticleConstants.LOADTYPE_LOAD_MORE, false);
    }

    @PostMapping("/loadnew")
    @RateLimit(permitsPerSecond = 1, limitType = RateLimit.LimitType.IP)
    public ResponseResult<List<Article>> loadNew(@RequestBody ArticleHomeDto dto){
        return articleService.listArticle(dto, ArticleConstants.LOADTYPE_LOAD_NEW, false);
    }
}
