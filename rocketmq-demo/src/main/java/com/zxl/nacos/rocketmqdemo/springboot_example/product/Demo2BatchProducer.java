package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo02BatchMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@Slf4j
public class Demo2BatchProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/sendBatch")
    public SendResult sendBatch(){
        ArrayList<Message> messageList = new ArrayList<>();
        for(int i = 0;i < 20;i++){
            Demo02BatchMessage message = new Demo02BatchMessage();
            message.setId(i);

            Message<Demo02BatchMessage> message1 = MessageBuilder.withPayload(message).build();
            messageList.add(message1);
        }

        // 批量消息是指将多条消息合并为一个批量消息，一次发送出去。这样做的好处是可以减少网络IO，提高吞吐量。一个批量消息最大限制为4M。
        return rocketMQTemplate.syncSend(Demo02BatchMessage.TOPIC,messageList,30*1000L);
    }

}
