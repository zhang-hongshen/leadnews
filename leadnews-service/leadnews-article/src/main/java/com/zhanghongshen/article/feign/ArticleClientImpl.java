package com.zhanghongshen.article.feign;

import com.zhanghongshen.apis.article.ArticleClient;
import com.zhanghongshen.article.service.ArticleService;
import com.zhanghongshen.model.article.dto.ArticleDto;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.common.enums.AppHttpCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleClientImpl implements ArticleClient {

    @Autowired
    private ArticleService articleService;

    @PostMapping("/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        if(dto == null){
            return ResponseResult.error(AppHttpCodeEnum.PARAM_INVALID);
        }
        return articleService.saveArticle(dto);
    }

    @DeleteMapping("/{articleId}")
    @Override
    public ResponseResult deleteArticle(@PathVariable("articleId") Long articleId) {
        articleService.removeById(articleId);
        return ResponseResult.success();
    }
}
