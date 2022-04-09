package com.zxl.nacos.kafkademo.springboot_example;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * 强文推荐：支持百万级TPS，Kafka是怎么做到的？答案藏在这10张图里： https://mp.weixin.qq.com/s/LO5hlm_GbH8ytoABbnA5CQ
 *           刨根问底，Kafka消息中间件到底会不会丢消息： https://mp.weixin.qq.com/s/TScpvjpIlyZyj62e8EwOPg
 */
@RestController
@Slf4j
public class KafkaController {
    private final static String TOPIC_NAME = "my-replicated-topic";

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @GetMapping("/send")
    public String send() throws ExecutionException, InterruptedException {
        for(int i=0;i <50;i++) {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC_NAME, 0, "key"+i, "this is +"+i+" msg");
            SendResult<String, String> sendResult = future.get();
            log.info("生成者发送成功。sendResult.ProducerRecord:{},sendResult.RecordMetadata:{}", sendResult.getProducerRecord().toString(), sendResult.getRecordMetadata().toString());
        }
        return "生成者发送成功";
    }

}
