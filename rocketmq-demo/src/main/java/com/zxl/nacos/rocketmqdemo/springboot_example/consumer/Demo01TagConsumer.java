package com.zxl.nacos.rocketmqdemo.springboot_example.consumer;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo01TagMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


/**
 *  SpringBoot集成RocketMQ，消费者部分的核心就在这个@RocketMQMessageListener注解上。
 *      消息过滤可以由里面的selectorType属性和selectorExpression来定制
 *      消息有序消费还是并发消费则由consumeMode属性定制。
 *      消费者是集群部署还是广播部署由messageModel属性定制。
 *      然后关于事务消息，还需要配置一个事务消息监听器：
 *  短板：rocket-client API中消费者可以确认消息是否成功
 *       而SpringBoot集成RocketMQ的消费者好像没法确认消息是否成功。
 *
 *  消费者与消息队列小结：
 *       消息队列负载由RebalanceService线程默认每隔20s进行一次消息队列负载，根据当前消费者组内消费者个数与主题队列数量按照某一种负载算法进行队列分配，
 *   分配原则：同一个消费者可以分配多个消息消费队列，同一个消息消费队列同一时间只会分配给一个消费者。
 *       消息拉取由PullMessageService线程根据RebalanceService线程创建的拉取任务进行拉取，默认每次拉取32条消息，提交给消费者消费线程后继续下一次消息拉取。
 *   如果消息消费过慢产生消息堆积会触发消息消费拉取流控。
 *       并发消息消费指消费线程池中的线程可以并发对同一个消息队列的消息进行消费，消费成功后，取出消息队列中最小的消息偏移量作为消费进度偏移量储存在消息消费进度储存文件中，
 *   集群模式消息消费进度储存在Broker(消息服务器)，广播模式消息消费进度储存在消费者端。
 *       RocketMQ不支持任意精度的定时调度消息，只支持自定义的消息延迟级别，例如1s、2s、5s等，可通过在broker配置文件中设置messageDelayLevel。
 */
@Slf4j
@Component
@RocketMQMessageListener(
        topic = Demo01TagMessage.TOPIC,
        consumerGroup = "demo01-A-consumer-group-" + Demo01TagMessage.TOPIC,
        selectorExpression = "TagA || TagB", //  tag will not be selected by selectorExpression
        consumeMode = ConsumeMode.CONCURRENTLY,
        messageModel= MessageModel.CLUSTERING
)
public class Demo01TagConsumer implements RocketMQListener<MessageExt> {
    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("[onMessage][线程编号:{} 消息内容：{}]", Thread.currentThread().getId(), messageExt);
    }
}



