package com.zhanghongshen.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhanghongshen.model.article.pojo.ArticleConfig;

public interface ArticleConfigService extends IService<ArticleConfig> {

    void updateByArticleId(Long articleId, Short enable);
}
