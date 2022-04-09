package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo01TagMessage;
import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo06TransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Demo01TagProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/syncSend")
    public void syncSend() {

        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            Demo01TagMessage payload = new Demo01TagMessage();
            payload.setId(i);

            //尝试在Header中加入一些自定义的属性。
            Message<Demo01TagMessage> message = MessageBuilder.withPayload(payload)
                    //发到监听器里后，这个自己设定的TAGS属性会丢失。但是下面那个KEYS不会丢失。
//                    .setHeader(RocketMQHeaders.TAGS, tags[i % tags.length])
                    .setHeader(RocketMQHeaders.KEYS,"KeysId_"+i)
                    //MyProp在事务监听器里也能拿到，为什么就单单这个RocketMQHeaders.TAGS拿不到？这只能去调源码了。
                    .setHeader("MyProp", "MyProp_" + i)
                    .build();
            // destination=topic:tags中tags不会丢失
            String destination = Demo01TagMessage.TOPIC + ":" + tags[i % tags.length];

            SendResult sendResult = rocketMQTemplate.syncSend(destination, message); //tag will not be selected by consumer @RocketMQMessageListener selectorExpression
            log.info("同步发送消息:{} ",sendResult);
        }

    }

    @GetMapping("/asyncSend")
    public void asyncSend(Integer id) {
        Demo01TagMessage message = new Demo01TagMessage();
        message.setId(id);

        // 异步发送消息
        rocketMQTemplate.asyncSend(Demo01TagMessage.TOPIC, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送消息成功：{}",sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步发送消息失败。原因：{}",throwable.getMessage());
            }
        });
    }


    @GetMapping("/onewaySend")
    public void onewaySend(Integer id) {
        Demo01TagMessage message = new Demo01TagMessage();
        message.setId(id);

        // oneway 发送消息
        rocketMQTemplate.sendOneWay(Demo01TagMessage.TOPIC, message); // 没有返回值，不保障一定成功
    }

}
