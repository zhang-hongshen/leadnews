package com.zhanghongshen.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.model.article.dto.ArticleBehaviorDto;
import com.zhanghongshen.model.article.dto.ArticleDto;
import com.zhanghongshen.model.article.dto.ArticleHomeDto;
import com.zhanghongshen.model.article.pojo.Article;
import com.zhanghongshen.common.dto.ResponseResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ArticleService extends IService<Article> {

    ResponseResult<List<Article>> listArticle(ArticleHomeDto dto, short loadType,
                                              boolean firstPage);

    ResponseResult saveArticle(ArticleDto dto);


    void updateScore(ArticleBehaviorDto dto);

    void deleteArticle(Long articleId);
}
