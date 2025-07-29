package com.zhanghongshen.article.listener;

import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.article.service.ArticleConfigService;
import com.zhanghongshen.common.constants.TopicConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ArticleDownListener {

    @Autowired
    private ArticleConfigService articleConfigService;

    @KafkaListener(topics = TopicConstants.TOPIC_WM_NEWS_UP_OR_DOWN)
    public void onMessage(String message){
        if(StringUtils.isBlank(message)){
            return;
        }
        log.info("[ArticleDownListener] receive message {}", message);
        Map<String, Object> map = JSON.parseObject(message);
        Long articleId = Long.valueOf(String.valueOf(map.get("articleId")));
        Short enable = Short.valueOf(String.valueOf(map.get("enable")));
        articleConfigService.updateByArticleId(articleId, enable);

    }
}
