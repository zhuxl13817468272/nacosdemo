package com.zxl.nacos.rabbitmqdemo.producer;

import com.alibaba.fastjson.JSONObject;
import com.zxl.nacos.rabbitmqdemo.pojo.Person;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class RabbitSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpTemplate rabbitmqTemplate;

    /**
     * 简单的消息模型 -- 使用默认的交换机，故不需要指定交换机。消息直接发到queue
     *      "hello world" -- the simplest thing that does something
     *      "work queues" -- Distributing tasks among workers
     *      两种类型区别：如果一个队列且只有一个消费者，则一个消费者来消费消息 hello world
     *                   如果一个队列且有多个消费者，则每个消费者轮询消费  work queue
     * @return
     */
    @GetMapping("/send")
    public String send(){
        rabbitTemplate.convertAndSend("myQueueD","now"+new Date());// 因为没有交换机，routingkey就表示队列名称，如果消费有多个绑定这个队列名，则是work queues模式，消费者会分担压力
//        rabbitTemplate.convertAndSend("","myQueueC","now"+new Date());// 经验证，这句代码和上句代码作用相同
        return "myQueue 队列消息发送成功";
    }






    /**
     * publish/subscribe -- Fanout-Exchange消息模型
     *      Sending messages to many consumers at once 所有跟指定的exchange绑定的队列都会收到消息-广播式
     * @return
     */
    @GetMapping("/sendFanout")
    public String sendFanout(){
        CorrelationData cId = new CorrelationData(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("myFanoutA", "fanoutA", "now" + new Date(), cId); // 绑定routingkey没有意义
//        rabbitTemplate.convertAndSend("myFanoutA", "", "now" + new Date(), cId); // 经验证，这句代码和上句代码作用相同
        return "sendFanout 队列消息发送成功";
    }








    /**
     *  发送者确认 -- 消息是否已发送到交换机(Exchange)
     *      如果交换机接收到数据，ack为true、cause会返回null；
     *      如果没有这个交换机,ack为false、cause会返回原因且不会触发ReturnCallback方法
     *
     */
    public static RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {
            System.out.println("ConfirmCallback:     " + "相关数据：" + correlationData);
            System.out.println("ConfirmCallback:     " + "确认情况：" + ack); // 如果交换机接收到数据，ack为true；如果发送失败（没有这个交换机）,ack为false
            System.out.println("ConfirmCallback:     " + "原因：" + cause);
        }
    };

    /**
     *  发送者确认 -- 消息是否已发送到队列(Queue)，如果没有找到队列会退回信息
     *      如果消息已发送到队列，则此方法不会被触发；
     *      如果没有找到队列，则此方法会被触发。
     */
    public static RabbitTemplate.ReturnCallback returnCallback = new RabbitTemplate.ReturnCallback() {
        @Override
        public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
            System.out.println("ReturnCallback:     " + "回应码：" + replyCode);  //  312
            System.out.println("ReturnCallback:     " + "回应信息：" + replyText); // NO_ROUTE
            System.out.println("ReturnCallback:     " + "消息：" + message);
            System.out.println("ReturnCallback:     " + "交换机：" + exchange);
            System.out.println("ReturnCallback:     " + "路由键：" + routingKey);
        }
    };
    /**
     * Routing -- Direct-Exchange+路由消息模型
     *      Receving messages selectively 队列与交换机 会用一个routingKey绑定起来，发布订阅指定的消息
     * @return
     */
    @GetMapping("/sendDirect")
    public String sendOrder(){
        rabbitTemplate.convertAndSend("myDirect","directA","now"+new Date());

        // 发送者确认 -- 消息已发送到交换机(Exchange)
        rabbitTemplate.setConfirmCallback(confirmCallback);
        // 如果没有到交换机会退回数据

        // 发送者确认 -- 消息已发送到队列(Queue)，如果没有找到队列会退回信息
        rabbitTemplate.setReturnCallback(returnCallback);
        return "sendDirect computer 消息发送成功";
    }


    /**
     * Topics -- Topic-Exchange+路由消息模型
     *      Receiving messages based on a pattern 队列与交换机 可用具有模式匹配符的routingKey绑定起来，发布订阅指定的消息
     * @return
     */
    @GetMapping("/sendTopic")
    public String sendTopic(){
        CorrelationData cId = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("生产者CorrelationData :" + cId);
        Person person = new Person();
        person.setName("archiz").setAge(28).setAddress("上海xxx");
        //  Routingkey 一般都是有一个或多个单词组成，多个单词之间以”.”分割，例如： item.insert
        rabbitTemplate.convertAndSend("myTopic", "topic.order.insert", JSONObject.toJSONString(person), cId);
        return "sendTopic 队列消息发送成功";
    }


}
