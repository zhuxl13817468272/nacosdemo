package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo02BatchMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RocketMQMessageListener(
        topic = Demo02BatchMessage.TOPIC,
        consumerGroup = "demo02-consumer-group-" + Demo02BatchMessage.TOPIC
)
public class Demo02BatchConsumer implements RocketMQListener<Demo02BatchMessage> {
    @Override
    public void onMessage(Demo02BatchMessage demo02BatchMessage) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), demo02BatchMessage);
    }
}
