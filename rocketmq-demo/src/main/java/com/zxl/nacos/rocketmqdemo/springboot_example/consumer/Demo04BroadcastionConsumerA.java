package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo04BroadcastingMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 */
@RestController
@Slf4j
@RocketMQMessageListener(
        topic = Demo04BroadcastingMessage.TOPIC,
        consumerGroup = "demo04-consumer-group-" + Demo04BroadcastingMessage.TOPIC,
        /*
          * 默认为集群状态(MessageModel.CLUSTERING)，每一条消息只会被同一个消费组中的一个实例消费到（这跟kafka和rabbitMQ的集群模式是一样的）
          * 而广播模式则把消息发给了所有订阅了对应主题的消费者，而不管消费者是不是在同一个消费者组。
         */
        messageModel = MessageModel.BROADCASTING
)
public class Demo04BroadcastionConsumerA implements RocketMQListener<Demo04BroadcastingMessage> {
    @Override
    public void onMessage(Demo04BroadcastingMessage demo04BroadcastingMessage) {
        log.info("[onMessage][线程编号:{} 广播消息内容：{}]", Thread.currentThread().getId(), demo04BroadcastingMessage);
    }
}
