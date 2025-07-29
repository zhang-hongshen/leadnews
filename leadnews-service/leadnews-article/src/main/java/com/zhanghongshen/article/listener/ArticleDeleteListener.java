package com.zhanghongshen.article.listener;

import com.zhanghongshen.article.service.ArticleService;
import com.zhanghongshen.common.constants.TopicConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleDeleteListener {

    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = TopicConstants.TOPIC_ARTICLE_DELETE)
    public void onMessage(String message){
        if(StringUtils.isBlank(message)){
            return;
        }
        log.info("[ArticleDeleteListener] receive message {}", message);
        articleService.deleteArticle(Long.valueOf(message));
    }
}

