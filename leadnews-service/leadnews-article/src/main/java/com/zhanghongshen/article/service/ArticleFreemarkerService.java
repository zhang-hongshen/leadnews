package com.zhanghongshen.article.service;


import com.zhanghongshen.model.article.pojo.Article;

public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minIO中
     * @param article
     * @param content
     */
    String uploadArticle(Article article, String content);
}
