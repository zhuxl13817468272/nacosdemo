package com.zxl.nacos.kafkademo.simple_example;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.*;

/**
 * 强文推荐：支持百万级TPS，Kafka是怎么做到的？答案藏在这10张图里： https://mp.weixin.qq.com/s/LO5hlm_GbH8ytoABbnA5CQ
 *           刨根问底，Kafka消息中间件到底会不会丢消息： https://mp.weixin.qq.com/s/TScpvjpIlyZyj62e8EwOPg
 */
public class ConsumerMain {
    private final static String TOPIC_NAME = "my-replicated-topic";
    private final static String CONSUMER_GROUP_NAME = "testGroup";

    private static Consumer<String,String> createConsumer(){
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"10.228.131.21:9092");
        properties.put(ConsumerConfig.GROUP_ID_CONFIG,CONSUMER_GROUP_NAME); // 消费者分组
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true); // 是否自动提交消费进度
        //properties.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"1000"); // 自动提交offset的间隔时间
        /**
         * 当消费主题的是一个新的消费组，或者指定offset的消费方式，offset不存在，那么应该如何消费
         * latest(默认) ：只消费自己启动之后发送到主题的消息
         * earliest：第一次从头开始消费，以后按照消费offset记录继续消费，这个需要区别于consumer.seekToBeginning(每次都从头开始消费)
         */
        //properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");
       // consumer给broker发送心跳的间隔时间，如果broker接收到心跳时有rebalance发生会,通过心跳响应将rebalance方案下发给consumer，这个时间可以稍微短一点
       // properties.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 1000);
       // 服务端broker多久感知不到一个consumer心跳就认为他故障了，会将其踢出消费组，对应的Partition也会被重新分配给其他consumer，默认是10秒
       // properties.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10 * 1000);
        // 如果两次poll操作间隔超过了这个时间，broker就会认为这个consumer处理能力太弱，会将其踢出消费组，将分区分配给别的consumer消费
        //properties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 30 * 1000);

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 因为我们消息的 key 和 value 都使用 String 类型，所以创建的 Producer 是 <String, String> 的泛型。
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        return kafkaConsumer;
    }

    public static void main(String[] args) {
        // 创建 KafkaConsumer 对象
        Consumer<String, String> consumer = createConsumer();

        // 订阅消息
        consumer.subscribe(Arrays.asList(TOPIC_NAME));
        // 消费指定分区
//        consumer.assign(Arrays.asList(new TopicPartition(TOPIC_NAME, 0)));

        //消息回溯消费
//        consumer.assign(Arrays.asList(new TopicPartition(TOPIC_NAME, 0)));
//        consumer.seekToBeginning(Arrays.asList(new TopicPartition(TOPIC_NAME, 0)));

        //指定offset消费
//        consumer.assign(Arrays.asList(new TopicPartition(TOPIC_NAME, 0)));
//        consumer.seek(new TopicPartition(TOPIC_NAME, 0), 10);

        //从指定时间点开始消费
//        List<PartitionInfo> topicPartitions = consumer.partitionsFor(TOPIC_NAME);
//        long fetchDataTime = new Date().getTime() - 1000 * 60 * 60; //从1小时前开始消费
//        Map<TopicPartition, Long> map = new HashMap<>();
//        for (PartitionInfo par : topicPartitions) {
//            map.put(new TopicPartition(TOPIC_NAME, par.partition()), fetchDataTime);
//        }
//        Map<TopicPartition, OffsetAndTimestamp> parMap = consumer.offsetsForTimes(map);
//        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : parMap.entrySet()) {
//            TopicPartition key = entry.getKey();
//            OffsetAndTimestamp value = entry.getValue();
//            if (key == null || value == null) continue;
//            Long offset = value.offset();
//            System.out.println("partition-" + key.partition() + "|offset-" + offset);
//            System.out.println();
//            //根据消费里的timestamp确定offset
//            if (value != null) {
//                consumer.assign(Arrays.asList(key));
//                consumer.seek(key, offset);
//            }
//        }

        // 拉取消息
        while (true){
            // poll() API 是拉取消息的长轮询
            // 拉取消息。如果拉取不到消息，阻塞等待最多 10 秒，或者等待拉取到消息。
            ConsumerRecords<String, String> records  = consumer.poll(Duration.ofSeconds(10));
            System.out.println("消息大小：===" + records);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("收到消息：partition = %d,offset = %d, key = %s, value = %s%n", record.partition(),
                        record.offset(), record.key(), record.value());
            }
            if (records.count() > 0) {
                // 手动同步提交offset，当前线程会阻塞直到offset提交成功
                // 一般使用同步提交，因为提交之后一般也没有什么逻辑代码了
                consumer.commitSync();

                // 手动异步提交offset，当前线程提交offset不会阻塞，可以继续处理后面的程序逻辑
//                consumer.commitAsync(new OffsetCommitCallback() {
//                    @Override
//                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
//                        if (exception != null) {
//                            System.err.println("Commit failed for " + offsets);
//                            System.err.println("Commit failed exception: " + exception.getStackTrace());
//                        }
//                    }
//                });

            }
        }

    }
}
