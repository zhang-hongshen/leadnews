package com.zhanghongshen.kafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.kstream.ValueMapper;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class StreamTest {

    public static void main(String[] args) {

        Properties properties = getProperties();

        StreamsBuilder streamsBuilder = new StreamsBuilder();

        streamProcessor(streamsBuilder);

        KafkaStreams kafkaStreams = new KafkaStreams(streamsBuilder.build(), properties);
        kafkaStreams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-stream-test");

        return properties;
    }

    private static void streamProcessor(StreamsBuilder streamsBuilder) {
        //创建kstream对象，同时指定从那个topic中接收消息
        KStream<String, String> stream = streamsBuilder.stream("test-leadnews-topic",
                Consumed.with(Serdes.String(), Serdes.String()));
        stream.flatMapValues((ValueMapper<String, Iterable<String>>) value -> {
                System.out.println("Message："+value);
                return Arrays.asList(value.split(" "));
        }).groupBy((key,value)->value)
                .windowedBy(TimeWindows.of(Duration.ofSeconds(5)))
                .count()
                .toStream()
                .map((key,value)->{
                    System.out.println("key:"+ key +", value:"+value);
                    return new KeyValue<>(key.key(), value.toString());
                })
                .to("test-leadnews-topic-out");

    }
}
