package com.zhanghongshen.search.listener;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.model.search.vo.SearchArticleVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class ArticleIndexCreateListener {

    private final ElasticsearchClient esClient;

    @KafkaListener(topics = TopicConstants.TOPIC_ARTICLE_ES_SYNC)
    public void onMessage(String message){
        if(StringUtils.isNotBlank(message)){
            log.info("ArticleIndexCreateListener, message={}",message);

            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);
            try {
                esClient.index(builder -> builder.index("app_info_article")
                        .id(searchArticleVo.getId().toString())
                        .document(searchArticleVo));
            } catch (IOException e) {
                log.error("es create index error.",e);
            }
        }
    }
}
