package com.zxl.nacos.rabbitmqdemo.consumer;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 *  延时队列： ttl + dlx   场景：下单30min后，发送短信提醒
 *      在一个30min消息过期的队列A中，设置参数x-dead-letter-exchange=B_exhcange、x-dead-letter-routing-key = dlx.xxx ，
 *  而B_exhcange的routing_key =  dlx.#的消息路由到另一个队列B中。30min后一个消息MsgA过期了，就会被放到B_exhcange中，进而路由到队列B。最终被绑定队列B的消费者接受。
 *
 *  “死信” 队列，是一个普通的队列，存放“死信”。何为“死信”？一个消息当过期了、队列满了被遗弃、消息被消费端拒绝接受且不重回队列时，会成为死信。
 */
@RestController
public class WelcomeRecepter {

    @RabbitListener(bindings = @QueueBinding(value = @Queue("myQueueD"),exchange = @Exchange("myExchangeC")))
    public void process(Message message, Channel channel)  {
        System.out.println(" myQueueC: 1 -- message：" + message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("myQueueD"),exchange = @Exchange("myExchangeC")))
    public void processB(Message message, Channel channel){
        System.out.println("myQueueC: 2 -- message：" + message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("myQueueD"),exchange = @Exchange("myExchangeC")))
    public void processC(Message message, Channel channel) throws IOException{
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        // 消费者确认 -- 手动确认接收到此消息。
        // ChannelN.basicAck(long deliveryTag, boolean multiple)
        // 第二个参数表示是否批量处理。一般用false 。为false,则只确认deliveryTag这条信息;为true，则可以一次性ack(确认)所有小于deliveryTag的消息，
        channel.basicAck(deliveryTag,false);
        System.out.println("myQueueC: 3 -- deliveryTag = " + deliveryTag +" ; message" + message);
    }





    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueFanoutA"),exchange = @Exchange(value = "myFanoutA",type = ExchangeTypes.FANOUT),key = "fanoutA"))
    public void processFanoutA(Message message, Channel channel){
        try{
            Thread.sleep(1000);
        }catch (Exception e){

        }
        System.out.println("myFanout queueFanoutA:"+message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueFanoutB"),exchange = @Exchange(value = "myFanoutA",type = ExchangeTypes.FANOUT),key = "fanoutB"))
    public void processFanoutB(Message message, Channel channel){
        try{
            Thread.sleep(2000);
        }catch (Exception e){

        }
        System.out.println("myFanout queueFanoutB:"+message);
    }






    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueDirectA"),exchange = @Exchange("myDirect"),key = "directA"))
    public void processDirectA(Message message, Channel channel){
        System.out.println("myDirect queueDirectA:" + message);
    }

    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueDirectB"),exchange = @Exchange(value = "myDirect"),key = "directB"))
    public void processDirectB(Message message, Channel channel){
        System.out.println("myDirect queueDirectB:" + message);
    }




    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueTopicA"),exchange = @Exchange(value = "myTopic",type = ExchangeTypes.TOPIC),key = "topic.*"))
    public void processTopicA(Message message, Channel channel) throws IOException {
//        (  Body:'{"address":"上海xxx","age":28,"name":"archiz"}'
//           MessageProperties [headers={spring_listener_return_correlation=e0ba42fd-54c8-43b3-92cb-5ebf25d99d49, spring_returned_message_correlation=829a9f98-bdd4-432a-af8d-2db7754a5d29},
//                              contentType=text/plain, contentEncoding=UTF-8, contentLength=0, receivedDeliveryMode=PERSISTENT, priority=0, redelivered=false,
//                              receivedExchange=myTopic, receivedRoutingKey=topicA, deliveryTag=1, consumerTag=amq.ctag-br-RM42LpaRdgP1Sc13rwg, consumerQueue=queueTopicA ee]  )
        long deliveryTag = message.getMessageProperties().getDeliveryTag(); //当前消息数据的唯一id
        try {
            String consumerQueue = message.getMessageProperties().getConsumerQueue();
            System.out.println("myTopic queueTopicA:" + message);

            // 如果是在限流的情况下，一定不能设置自动签收，autoAck = false; 一定要手动的进行消费
            // ChannlN.basicQos(int prefetchCount, boolean global)
            // 第一个参数：prefetchCount 一般工作中设置为1，当这条消息deliveryTag被Ack或者Reject后，才会继续消费下个消息
            // 第二个参数：限流策略，是否将限流设置应用于channel。RabbitMq上有两个级别：1.channel,2.consumer。这次参数为true时表示级别为channel,为false表示级别为consumer。
            channel.basicQos(1,false); // Quality of Service，服务质量

            /**
             *  业务处理...
             */

            // 消费者确认 -- 第二个参数表示是否批量处理，为true，则可以一次性ack(确认)所有小于deliveryTag的消息
            channel.basicAck(deliveryTag,false); //只确认此条消息（deliveryTag）,就像取快递一样，根据取件唯一标识deliveryTag，只取这个消息

        }catch (Exception e){
            // redelivered = true 表明该消息是重复处理消息  反之 表明该消息是第一次消费
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            /**
             * 这里对消息重入队列做设置，例如将消息序列化缓存至Redis,并记录重入队列次数
             * 如果该消息重入队列次数达到一定次数，比如3次，将不再重入队列，直接拒绝
             * 这时候需要对消息做补偿机制处理
             */

            if(redelivered) {
                /**
                 *  1.对于重复处理的队列消息做补偿机制处理
                 *  2.从队列中移除该消息，requeue=fasle表示不把消息重新放入原目标队列
                 */
                // 消费者确认 -- 手动确认拒绝此消息。
                // long deliveryTag, boolean requeue
                // 第二个参数为true,数据重新丢回队列里，下次还会消费这个消息；为false,就是告诉服务器，我已经知道此消息数据了，因为一些原因拒绝它，而且服务器会把此消息丢掉，不会在有此信息发来了
                channel.basicReject(deliveryTag, false); //拒绝消息
                // long deliveryTag, boolean multiple, boolean requeue
//                channel.basicNack(deliveryTag,false,false); // 这句代码和上句代码作用相同
            }

        }
    }


    // Routingkey 一般都是有一个或多个单词组成，多个单词之间以”.”分割，例如： item.insert
    //通配符规则：# 匹配一个或多个词，* 匹配不多不少恰好1个词，例如：item.# 能够匹配 item.insert.abc 或者 item.insert，item.* 只能匹配 item.insert
    @RabbitListener(bindings = @QueueBinding(value = @Queue("queueTopicB"),exchange = @Exchange(value = "myTopic",type = ExchangeTypes.TOPIC),key = "topic.#"))
    public void processTopicB(Message message, Channel channel){
        System.out.println("myTopic queueTopicB:" + message);
    }


}
