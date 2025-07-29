package com.zhanghongshen.article.listener;

import com.alibaba.fastjson2.JSON;
import com.zhanghongshen.common.constants.TopicConstants;
import com.zhanghongshen.model.article.dto.ArticleBehaviorDto;
import com.zhanghongshen.model.message.UpdateArticleMess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class ArticleBehaviorHandler {

    @Bean
    public KStream<String,String> kStream(StreamsBuilder streamsBuilder){
        //接收消息
        KStream<String,String> stream = streamsBuilder.stream(TopicConstants.TOPIC_HOT_ARTICLE_SCORE);
        //聚合流式处理
        stream.map((key,value)->{
                    UpdateArticleMess mess = JSON.parseObject(value, UpdateArticleMess.class);
                    //重置消息的key:1234343434   和  value: likes:1
                    return new KeyValue<>(mess.getArticleId().toString(),
                            mess.getType().name()+":"+ mess.getDiff());
                })
                //按照文章id进行聚合
                .groupBy((key,value)-> key)
                //时间窗口
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                .aggregate(() -> "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0",
                        (key, value, aggValue) -> {
                    if(StringUtils.isBlank(value)){
                        return aggValue;
                    }
                    String[] aggAry = aggValue.split(",");
                    int col = 0, com=0, lik=0, vie=0;
                    for (String agg : aggAry) {
                        String[] split = agg.split(":");
                        switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])){
                            case COLLECTION:
                                col = Integer.parseInt(split[1]);
                                break;
                            case COMMENT:
                                com = Integer.parseInt(split[1]);
                                break;
                            case LIKES:
                                lik = Integer.parseInt(split[1]);
                                break;
                            case VIEWS:
                                vie = Integer.parseInt(split[1]);
                                break;
                        }
                    }
                    String[] valAry = value.split(":");
                    switch (UpdateArticleMess.UpdateArticleType.valueOf(valAry[0])){
                        case COLLECTION:
                            col += Integer.parseInt(valAry[1]);
                            break;
                        case COMMENT:
                            com += Integer.parseInt(valAry[1]);
                            break;
                        case LIKES:
                            lik += Integer.parseInt(valAry[1]);
                            break;
                        case VIEWS:
                            vie += Integer.parseInt(valAry[1]);
                            break;
                    }
                    String formatStr = String.format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d", col, com, lik, vie);
                    log.info("article id {}, {}", key, formatStr);
                    return formatStr;
                }, Materialized.as("hot-atricle-stream-count-001"))
                .toStream()
                .map((key,value)->
                        new KeyValue<>(key.key(), formatObj(key.key(),value)))
                //发送消息
                .to(TopicConstants.TOPIC_HOT_ARTICLE_BEHAVIOR_HANDLE);

        return stream;


    }

    public String formatObj(String articleId, String value){
        ArticleBehaviorDto dto = new ArticleBehaviorDto();
        dto.setArticleId(Long.valueOf(articleId));
        //COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0
        String[] valAry = value.split(",");
        for (String val : valAry) {
            String[] split = val.split(":");
            switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])){
                case COLLECTION:
                    dto.setCollect(Integer.parseInt(split[1]));
                    break;
                case COMMENT:
                    dto.setComment(Integer.parseInt(split[1]));
                    break;
                case LIKES:
                    dto.setLike(Integer.parseInt(split[1]));
                    break;
                case VIEWS:
                    dto.setView(Integer.parseInt(split[1]));
                    break;
            }
        }
        log.info("聚合消息处理之后的结果为:{}",JSON.toJSONString(dto));
        return JSON.toJSONString(dto);

    }
}
