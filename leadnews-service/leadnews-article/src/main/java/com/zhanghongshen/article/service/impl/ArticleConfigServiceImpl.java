package com.zhanghongshen.article.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.article.mapper.ArticleConfigMapper;
import com.zhanghongshen.article.service.ArticleConfigService;
import com.zhanghongshen.model.article.pojo.ArticleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ArticleConfigServiceImpl extends ServiceImpl<ArticleConfigMapper, ArticleConfig>
        implements ArticleConfigService {


    /**
     * 修改文章配置
     * @param articleId Article Id
     */
    @Override
    public void updateByArticleId(Long articleId, Short enable) {
        boolean isDown = !enable.equals((short) 1);

        //修改文章配置
        update(Wrappers.<ArticleConfig>lambdaUpdate()
                .eq(ArticleConfig::getArticleId, articleId)
                .set(ArticleConfig::getDown, isDown));

    }
}
