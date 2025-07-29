package com.zhanghongshen.article.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhanghongshen.article.mapper.ArticleConfigMapper;
import com.zhanghongshen.article.mapper.ArticleContentMapper;
import com.zhanghongshen.article.mapper.ArticleMapper;
import com.zhanghongshen.article.service.ArticleFreemarkerService;
import com.zhanghongshen.article.service.ArticleService;
import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.constants.ArticleConstants;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.model.article.dto.ArticleBehaviorDto;
import com.zhanghongshen.model.article.dto.ArticleDto;
import com.zhanghongshen.model.article.dto.ArticleHomeDto;
import com.zhanghongshen.model.article.pojo.Article;
import com.zhanghongshen.model.article.pojo.ArticleConfig;
import com.zhanghongshen.model.article.pojo.ArticleContent;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.search.vo.SearchArticleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
    implements ArticleService {

    private final ArticleFreemarkerService articleFreemarkerService;

    private final ArticleMapper articleMapper;

    private final ArticleConfigMapper articleConfigMapper;

    private final ArticleContentMapper articleContentMapper;

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final CacheService cacheService;

    private final TransactionTemplate transactionTemplate;

    @Override
    public ResponseResult<List<Article>> listArticle(ArticleHomeDto dto, short loadType,
                                                     boolean firstPage) {
        if(firstPage) {
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if(StringUtils.isNotBlank(jsonStr)){
                List<Article> articles = JSON.parseArray(jsonStr, Article.class);
                return ResponseResult.success(articles);
            }
        }

        dto.checkParam();

        if(loadType != ArticleConstants.LOADTYPE_LOAD_MORE
                && loadType != ArticleConstants.LOADTYPE_LOAD_NEW) {
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //文章频道校验
        if(StringUtils.isBlank(dto.getTag())) {
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null)  {
            dto.setMaxBehotTime(new Date());
        }
        if(dto.getMinBehotTime() == null) {
            dto.setMinBehotTime(new Date());
        }

        return ResponseResult.success(articleMapper.listArticle(dto, loadType));
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {

        Article article = new Article();
        BeanUtils.copyProperties(dto, article);

        if(dto.getArticleId() == null) {

            transactionTemplate.executeWithoutResult(transactionStatus -> {
                // save article
                save(article);

                // save article's config
                ArticleConfig apArticleConfig = new ArticleConfig(article.getId());
                articleConfigMapper.insert(apArticleConfig);

                // save article's content
                ArticleContent articleContent = new ArticleContent();
                articleContent.setArticleId(article.getId());
                articleContent.setContent(dto.getContent());
                articleContentMapper.insert(articleContent);
            });

        } else {
            article.setId(dto.getArticleId());
            transactionTemplate.executeWithoutResult(transactionStatus -> {
                // update article
                updateById(article);

                // update article's content
                articleContentMapper.update(
                        null,
                        Wrappers.<ArticleContent>lambdaUpdate()
                                .set(ArticleContent::getContent, dto.getContent())
                                .eq(ArticleContent::getArticleId, article.getId())
                );
            });
        }

        afterSaveArticle(article, dto.getContent());

        return ResponseResult.success(article.getId());
    }

    @Async
    protected void afterSaveArticle(Article article, String content) {
        String filepath = articleFreemarkerService.uploadArticle(article, content);
        update(Wrappers.<Article>lambdaUpdate()
                .eq(Article::getId, article.getId())
                .set(Article::getStaticUrl, filepath));
        sendCreateSearchIndexMsg(article, content, filepath);
    }

    private void sendCreateSearchIndexMsg(Article article, String content, String filepath) {
        SearchArticleVo searchArticleVo = new SearchArticleVo();
        BeanUtils.copyProperties(article, searchArticleVo);
        searchArticleVo.setContent(content);
        searchArticleVo.setStaticUrl(filepath);
        kafkaTemplate.send(TopicConstants.TOPIC_ARTICLE_ES_SYNC, JSON.toJSONString(searchArticleVo));
    }

    @Override
    public void updateScore(ArticleBehaviorDto dto) {

        Article article = updateArticleBehavior(dto);
        updateCache(article, article.getScore(), ArticleConstants.HOT_ARTICLE_FIRST_PAGE + article.getChannelId());
        updateCache(article, article.getScore(), ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

    }

    private Article updateArticleBehavior(ArticleBehaviorDto dto) {
        Article article = getById(dto.getArticleId());
        article.setCollection(article.getCollection() == null ? 0 : article.getCollection() + dto.getCollect());
        article.setComment(article.getComment() == null ? 0 : article.getComment() + dto.getComment());
        article.setLikes(article.getLikes() == null ? 0 : article.getLikes() + dto.getLike());
        article.setViews(article.getViews() == null ? 0 : article.getViews() + dto.getView());
        updateById(article);
        return article;
    }

    private void updateCache(Article article, Integer score, String key) {
        String articleListStr = cacheService.get(key);
        if (StringUtils.isBlank(articleListStr)) {
            return;
        }

        List<Article> hotArticles = JSON.parseArray(articleListStr, Article.class);

        if (hotArticles.stream()
                .noneMatch(hotArticle -> hotArticle.getId().equals(article.getId()))) {
            if (hotArticles.size() >= 30) {
                hotArticles = hotArticles.stream()
                        .sorted(Comparator.comparing(Article::getScore).reversed())
                        .collect(Collectors.toList());
                Article lastHot = hotArticles.get(hotArticles.size() - 1);
                if (lastHot.getScore() < score) {
                    hotArticles.remove(lastHot);
                    Article hot = new Article();
                    BeanUtils.copyProperties(article, hot);
                    hotArticles.add(hot);
                }
            } else {
                Article hot = new Article();
                BeanUtils.copyProperties(article, hot);
                hotArticles.add(hot);
            }
        }

        hotArticles = hotArticles.stream()
                .sorted(Comparator.comparing(Article::getScore).reversed())
                .collect(Collectors.toList());

        cacheService.set(key, JSON.toJSONString(hotArticles));
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        removeById(articleId);
        articleContentMapper.delete(Wrappers.<ArticleContent>lambdaUpdate()
                .eq(ArticleContent::getArticleId, articleId));
        articleConfigMapper.delete(Wrappers.<ArticleConfig>lambdaUpdate()
                .eq(ArticleConfig::getArticleId, articleId));
    }

}
