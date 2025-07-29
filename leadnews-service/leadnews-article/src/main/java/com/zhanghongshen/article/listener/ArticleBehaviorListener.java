package com.zhanghongshen.article.listener;


import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.article.service.ArticleService;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.model.article.dto.ArticleBehaviorDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ArticleBehaviorListener {

    @Autowired
    private ArticleService articleService;

    @KafkaListener(topics = TopicConstants.TOPIC_HOT_ARTICLE_BEHAVIOR_HANDLE)
    public void onMessage(String message){
        if(StringUtils.isBlank(message)){
            return;
        }
        log.info("[ArticleBehaviorListener] receive message: {}", message);
        ArticleBehaviorDto dto = JSON.parseObject(message, ArticleBehaviorDto.class);
        articleService.updateScore(dto);
    }


}
