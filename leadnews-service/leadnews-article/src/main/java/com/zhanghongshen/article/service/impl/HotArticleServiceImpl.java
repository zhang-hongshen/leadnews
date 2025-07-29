package com.zhanghongshen.article.service.impl;

import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.apis.wemedia.WemediaClient;
import com.zhanghongshen.article.mapper.ArticleMapper;
import com.zhanghongshen.article.service.HotArticleService;
import com.zhanghongshen.common.cache.CacheService;
import com.zhanghongshen.common.constants.ArticleConstants;
import com.zhanghongshen.common.dto.ResponseResult;
import com.zhanghongshen.model.article.pojo.Article;
import com.zhanghongshen.model.wemedia.pojo.WmChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotArticleServiceImpl implements HotArticleService {

    private final ArticleMapper articleMapper;

    private final WemediaClient wemediaClient;

    private final CacheService cacheService;

    /**
     * 计算热点文章
     */
    @Override
    @Scheduled(cron = "0 0 2 * * ?")
    public void computeHotArticle() {
        // 1.查询前5天的文章数据
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -5000);
        List<Article> articles = articleMapper.listArticleAfter(calendar.getTime());

        // 3.为每个频道缓存30条分值较高的文章
        cacheHotArticle(articles);

    }

    /**
     * 为每个频道缓存30条分值较高的文章
     * @param articles
     */
    private void cacheHotArticle(List<Article> articles) {

        doCacheHotArticle(articles, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

        //每个频道缓存30条分值较高的文章
        ResponseResult<List<WmChannel>> responseResult = wemediaClient.listChannel();
        if(!responseResult.isSuccess()){
            log.error("[HotArticleService] listChannel error, code:{}, msg:{}",
                    responseResult.getCode(), responseResult.getErrorMessage());
            return;
        }
        List<WmChannel> wmChannels = responseResult.getData();

        for (WmChannel wmChannel : wmChannels) {
            //给文章进行排序，取30条分值较高的文章存入redis  key：频道id   value：30条分值较高的文章
            doCacheHotArticle(articles.stream()
                    .filter(article -> article.getChannelId().equals(wmChannel.getId()))
                    .collect(Collectors.toList()),
                    ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
        }
    }

    /**
     * 排序并且缓存数据
     * @param articles
     * @param key
     */
    private void doCacheHotArticle(List<Article> articles, String key) {
        articles = articles.stream()
                .sorted(Comparator.comparing(Article::getScore).reversed())
                .collect(Collectors.toList());
        if (articles.size() > 30) {
            articles = articles.subList(0, 30);
        }
        cacheService.set(key, JSON.toJSONString(articles));
    }

}
