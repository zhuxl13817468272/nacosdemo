package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo03DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RocketMQMessageListener(
        topic = Demo03DelayMessage.TOPIC,
        consumerGroup = "demo03-consumer-group-" + Demo03DelayMessage.TOPIC
)
public class Demo03DelayConsumer implements RocketMQListener<Demo03DelayMessage> {
    @Override
    public void onMessage(Demo03DelayMessage demo03DelayMessage) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), demo03DelayMessage);
    }
}
