package com.zxl.nacos.rabbitmqdemo.producer;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 *  消息确认 (生成者推送消息到交换机成功，消费者消费成功)
 */

@RestController
public class MessageConfirmController {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * Topic-Exchange+路由消息模型
     *      队列与交换机 可用具有模式匹配符的routingKey绑定起来，发布订阅指定的消息
     * @return
     */
    @GetMapping("/non-existent-exchange")
    public String sendTopic(){
        CorrelationData cId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("myDirect", "TestDirectRouting", "now" + new Date(), cId);

        // 确认消息已发送到交换机(Exchange)
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("ConfirmCallback:     "+"相关数据："+correlationData);
                System.out.println("ConfirmCallback:     "+"确认情况："+ack);
                System.out.println("ConfirmCallback:     "+"原因："+cause);
                if(ack){
                    System.out.println("消息发送成功。插入日志");
                }else {
                    System.out.println("消息发送失败。根据业务逻辑实现消息补偿机制");
                }
            }
        });

        // 确认消息已发送到队列(Queue)
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                System.out.println("ReturnCallback:     "+"消息："+message);
                System.out.println("ReturnCallback:     "+"回应码："+replyCode);
                System.out.println("ReturnCallback:     "+"回应信息："+replyText);
                System.out.println("ReturnCallback:     "+"交换机："+exchange);
                System.out.println("ReturnCallback:     "+"路由键："+routingKey);
            }
        });


        return "sendTopic 队列消息发送成功";
    }

}
