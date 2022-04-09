package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo05OrderMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Demo05OrderProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/syncSendOrderly")
    public void syncSendOrderly(){

        for (int i = 0; i < 4; i++) {
            int orderId = i;
            for(int j = 0 ; j <= 5 ; j ++){
                Demo05OrderMessage demo05OrderMessage = new Demo05OrderMessage();
                demo05OrderMessage.setOrderId(orderId);
                demo05OrderMessage.setId(j);

                /*
                 * 基于 hashKey 的哈希值取余，选择对应的队列
                 *      发送端：默认采取Round Robin轮询方式把消息发送到不同的MessageQueue(分区队列)
                 */
                SendResult sendResult = rocketMQTemplate.syncSendOrderly(Demo05OrderMessage.TOPIC, demo05OrderMessage, orderId + "");

                log.info("orderId = {},messageId = {},发送成功：{}",orderId,demo05OrderMessage.getId(),sendResult);
            }
        }

    }

    /**
     *  异步的
     */
    @GetMapping("/asyncSendOrderly")
    public void asyncSendOrderly(){

        for (int i = 0; i < 4; i++) {
            int orderId = i;
            for(int j = 0 ; j <= 5 ; j ++){
                Demo05OrderMessage demo05OrderMessage = new Demo05OrderMessage();
                demo05OrderMessage.setOrderId(orderId);
                demo05OrderMessage.setId(j);

                rocketMQTemplate.asyncSendOrderly(Demo05OrderMessage.TOPIC, demo05OrderMessage, orderId + "", new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.info("orderId = {},messageId = {},发送成功：{}",orderId,demo05OrderMessage.getId(),sendResult);
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        log.error("orderId = {},messageId = {},发送异常：{}",orderId,demo05OrderMessage.getId(),throwable.getMessage());
                    }
                });


            }
        }

    }

    @GetMapping("/onewaySendOrderly")
    public void onewaySendOrderly(){

        for (int i = 0; i < 4; i++) {
            int orderId = i;
            for(int j = 0 ; j <= 5 ; j ++){
                Demo05OrderMessage demo05OrderMessage = new Demo05OrderMessage();
                demo05OrderMessage.setOrderId(orderId);
                demo05OrderMessage.setId(j);

                rocketMQTemplate.sendOneWayOrderly(Demo05OrderMessage.TOPIC,demo05OrderMessage,orderId+"");
            }
        }

    }


}
