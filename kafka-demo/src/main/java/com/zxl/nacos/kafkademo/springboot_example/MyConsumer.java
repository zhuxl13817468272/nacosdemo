package com.zxl.nacos.kafkademo.springboot_example;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyConsumer {

//    @KafkaListener(groupId = "testGroup",topicPartitions = {
//            @TopicPartition(topic = "topic1",partitions = {"0", "1"}),
//            @TopicPartition(topic = "topic2",partitions = "0", partitionOffsets = @PartitionOffset(partition = "1",initialOffset = "100"))
//    },concurrency = "6") // 并发消费：concurrency就是同组下的消费者个数，就是并发消费数，必须小于等于分区总数
    @KafkaListener(topics = "my-replicated-topic", groupId = "testGroup1")
    public void listenGroup1a(ConsumerRecord<String,String> record, Acknowledgment ack){
        log.info("[onMessage]-testGroup1-a消费成功。线程编号:{}，消费者接受key:{},value:{}",Thread.currentThread(),record.key(),record.value());

        //业务处理A... 积分模块

        //手动提交offset
        ack.acknowledge();
    }

    // 集群消费的机制：集群消费模式下，相同 Consumer Group 的每个 Consumer 实例平均分摊消息
    @KafkaListener(topics = "my-replicated-topic", groupId = "testGroup1")
    public void listenGroup1b(ConsumerRecord<String,String> record, Acknowledgment ack){
        log.info("[onMessage]-testGroup1-b消费成功。线程编号:{}，消费者接受key:{},value:{}",Thread.currentThread(),record.key(),record.value());

        //业务处理B...短信模块

        //手动提交offset
        ack.acknowledge();
    }

    @KafkaListener(topics = "my-replicated-topic", groupId = "testGroup2" ) // 广播消息实现思路：  + "-" + "#{T(java.util.UUID).randomUUID()}")
    public void listenGroup2(ConsumerRecord<String,String> record, Acknowledgment ack){
        log.info("[onMessage]-testGroup2消费成功。线程编号:{}，消费者接受key:{},value:{}",Thread.currentThread(),record.key(),record.value());

        //手动提交offset
//        ack.acknowledge(); //当没有确认消息时，再次重启，该消费组会再次收到全部消息。（offset消费者和broker(_consumer_offsetTopic_xx)各维护一份，如果不提交（没有通讯）会不相同）
    }

}
