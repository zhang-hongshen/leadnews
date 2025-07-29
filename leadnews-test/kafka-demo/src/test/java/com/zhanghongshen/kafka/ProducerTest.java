package com.zhanghongshen.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class ProducerTest {

    public static void main(String[] args) {
        Properties properties = getProperties();

        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        // step 3: send message
        for(int i = 0; i < 10; i += 1) {
            ProducerRecord<String,String> record = new ProducerRecord<>("test-leadnews-topic", "1000" + i, "hello kafka");
            producer.send(record, (metadata, exception) -> {
                if(exception != null){
                    System.out.println(exception.getMessage());
                    return;
                }
                System.out.println("offset " + metadata.offset());
            });

        }
        // close connection
        producer.close();
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        //kafka的连接地址
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        //发送失败，失败的重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //消息key的序列化器
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //消息value的序列化器
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return properties;
    }
}