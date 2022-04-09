package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo06TransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.juli.logging.Log;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  事物流程的本质： 先发消息保证broker可用(发送half消息，对消息者不可见)，然后再执本地事务逻辑代码，待本地事务成功后，再发确认给broker，broker根据确认消息传给下游commit/rollback。
 *
 * 什么是事务消息。官网的介绍是：事务消息是在分布式系统中保证最终一致性的‘两阶段提交’的消息实现。他可以保证‘本地事务’执行与‘消息发送’两个操作的原子性，也就是这两个操作一起成功或者一起失败。
 * 对于复杂的分布式事务，RocketMQ提供的事务消息也是目前业内最佳的降级方案。
 *
 * 我们来理解下事务消息的编程模型。事务消息只保证消息‘发送者的本地事务’与‘发消息’这两个操作的原子性，因此，事务消息的示例只涉及到消息发送者，对于消息消费者来说，并没有什么特别的。
 *
*/
@RestController
@Slf4j
public class Demo06TransactionProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/sendMessageInTransaction")
    public void sendMessageInTransaction(){

        String[] tags = new String[] {"TagA", "TagB", "TagC", "TagD", "TagE"};
        for (int i = 0; i < 10; i++) {
            Demo06TransactionMessage payload = new Demo06TransactionMessage();
            payload.setId(i);

            //尝试在Header中加入一些自定义的属性。
            Message<Demo06TransactionMessage> message = MessageBuilder.withPayload(payload)
                    .setHeader(RocketMQHeaders.TRANSACTION_ID,"TransID_"+i)
                    //发到事务监听器里后，这个自己设定的TAGS属性会丢失。但是上面那个属性不会丢失。
                    .setHeader(RocketMQHeaders.TAGS,tags[i % tags.length])
                    //MyProp在事务监听器里也能拿到，为什么就单单这个RocketMQHeaders.TAGS拿不到？这只能去调源码了。
                    .setHeader("MyProp","MyProp_"+i)
                    .build();
            String destination =Demo06TransactionMessage.TOPIC+":"+tags[i % tags.length];

            //这里发送事务消息时，还是会转换成RocketMQ的Message对象，再调用RocketMQ的API完成事务消息机制。
            // sendMessageInTransaction(String destination, Message<?> message, Object arg)
            SendResult sendResult = rocketMQTemplate.sendMessageInTransaction(destination, message,destination);
            System.out.printf("%s%n", sendResult);

        }

    }


    @RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
    public class TransactionListenerImpl implements RocketMQLocalTransactionListener{

        // 发送消息执行这个方法
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
            log.info("[executeLocalTransaction][执行本地事务，消息：{} arg：{}]", message, o);
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        /**
         * 由于executeLocalTransaction方法返回RocketMQLocalTransactionState.UNKNOWN，所以过段时间后执行checkLocalTransaction。
         * 因为checkLocalTransaction方法返回RocketMQLocalTransactionState.COMMIT，消息者才能消费此条消息
         */
        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
            log.info("[checkLocalTransaction][回查消息：{}]", message);
            return RocketMQLocalTransactionState.COMMIT;
        }
    }

}
