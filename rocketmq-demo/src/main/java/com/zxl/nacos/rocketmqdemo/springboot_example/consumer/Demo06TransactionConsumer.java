package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo06TransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RocketMQMessageListener(
        topic = Demo06TransactionMessage.TOPIC,
        consumerGroup = "demo06-consumer-group-" + Demo06TransactionMessage.TOPIC,
        selectorExpression = "*"
)
public class Demo06TransactionConsumer implements RocketMQListener<Demo06TransactionMessage> {
    @Override
    public void onMessage(Demo06TransactionMessage demo06TransactionMessage) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), demo06TransactionMessage);
    }
}
