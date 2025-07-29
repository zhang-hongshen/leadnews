package com.zhanghongshen.kafka.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Arrays;

@Configuration
@Slf4j
public class KafkaStreamHelloListener {

    @Bean
    public KStream<String,String> kStream(StreamsBuilder streamsBuilder){
        KStream<String, String> stream = streamsBuilder.stream("test-leadnews-topic",
                Consumed.with(Serdes.String(), Serdes.String()));
        stream.flatMapValues((ValueMapper<String, Iterable<String>>) value -> {
                    log.info("Message：{}", value);
                    return Arrays.asList(value.split(" "));
                }).groupBy((key,value)->value)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .count()
                .toStream()
                .map((key,value)->{
                    log.info("key {}, value {}", key, value);
                    return new KeyValue<>(key.key(), value.toString());
                })
                .to("test-leadnews-topic-out");
        return stream;
    }
}
