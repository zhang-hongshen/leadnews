package com.zhanghongshen.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

public class ConsumerTest {

    public static void main(String[] args) {
        System.out.println(new Date());
        System.out.println(LocalDateTime.now());
//        Properties properties = getProperties();
//
//        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
//
//        consumer.subscribe(Collections.singletonList("test-leadnews-topic-out"));
//
//        //当前线程一直处于监听状态
//        while (true) {
//            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofMillis(1000));
//            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
//                System.out.println("key = " + consumerRecord.key() + ", value = " + consumerRecord.value() + ", partition = " + consumerRecord.partition());
//            }
//            // synchronously commit
//            consumer.commitSync();
//            // asynchronously commit
//            consumer.commitAsync();
//            // asynchronously commit + synchronously commit
//            consumer.commitAsync((map, e) -> {
//                if (e != null) {
//                    consumer.commitSync();
//                }
//            });
//        }

    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //消费者组
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "test-leadnews-consumer-group");
        //消息的反序列化器
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        return properties;
    }
}
