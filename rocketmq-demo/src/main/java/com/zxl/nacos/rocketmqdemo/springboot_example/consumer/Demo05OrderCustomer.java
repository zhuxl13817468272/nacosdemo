package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo05OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RocketMQMessageListener(
        topic = Demo05OrderMessage.TOPIC,
        consumerGroup = "demo05-consumer-group-" + Demo05OrderMessage.TOPIC,
        /*
         * 消费者：默认从多个MessageQueue上拉取消息，不保证顺序。Broker中一个队列内的消息是可以保证有序的
         *      MessageListenerOrderly对象要求RocketMQ内部通过锁队列的方式保证消息是一个队列一个队列来取的，即取完一个队列的消息后，再去取下一个队列的消息。
         */
        consumeMode = ConsumeMode.ORDERLY
)
public class Demo05OrderCustomer implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("[onMessage][线程编号:{} Body:{} QueueId:{} MsgId：{} ]", Thread.currentThread().getId(), new String(messageExt.getBody()),messageExt.getQueueId(),messageExt.getMsgId());

        // sleep 1 秒，用于查看顺序消费的效果
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignore) {
        }
    }
}
