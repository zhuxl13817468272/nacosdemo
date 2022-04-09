package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo04BroadcastingMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  同一消费者分组  的N个 Consumer 节点，都会消费这条发送的消息 -- 广播消费
 */
@RestController
@Slf4j
public class Demo04BroadcastingProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/syncSendBroadcasting")
    public SendResult syncSendBroadcasting(Integer id) {
        // 创建 Demo05Message 消息
        Demo04BroadcastingMessage message = new Demo04BroadcastingMessage();
        message.setId(id);

        // 同步发送消息
        return rocketMQTemplate.syncSend(Demo04BroadcastingMessage.TOPIC, message);
    }
}
