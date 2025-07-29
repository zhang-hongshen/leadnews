package com.zhanghongshen.kafka.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
@Slf4j
public class HelloListener {

    @KafkaListener(topics = "test-leadnews-topic")
    public void onMessage(String message){
        if(StringUtils.isEmpty(message)) {
            return;
        }
        log.info("received message:{}", message);
    }
}
