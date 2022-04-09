package com.zxl.nacos.rocketmqdemo.springboot_example.product;

import com.zxl.nacos.rocketmqdemo.springboot_example.vo.Demo03DelayMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


  /**
   *  延迟消息代码原理：
   *      延迟消息的核心使用方法就是在Message中设定一个MessageDelayLevel参数，对应18个延迟队列。
   *      Broker中会创建一个默认的Schedule_Topic主题，这个主题下有18个队列，对应18个延迟级别。
   *   消息发过来之后，会把消息存入Schedule_Topic主题中对应的队列，等延迟时间到了，再转发到目标队列，推给消息者进行消息
   */
@RestController
@Slf4j
public class Demo03DelayProducer {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/syncSendDelay")
    public SendResult syncSendDelay(Integer id, int delayLevel) {
        Demo03DelayMessage payload = new Demo03DelayMessage();
        payload.setId(id);

        Message<Demo03DelayMessage> message = MessageBuilder.withPayload(payload).build();

        // delayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        /**
         *  开源版本的RocketMQ中，对延迟消息并不支持任意时间的延迟设定(商业版本中支持)，而是只支持18个固定的延迟级别，
         *  1到18分别对应messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
         */
        SendResult sendResult = rocketMQTemplate.syncSend(Demo03DelayMessage.TOPIC, message, 30 * 1000L, delayLevel);
        return sendResult;
    }

    @GetMapping("/asyncSendDelay")
    public void asyncSendDelay(Integer id, int delayLevel) {
        Demo03DelayMessage payload = new Demo03DelayMessage();
        payload.setId(id);

        Message<Demo03DelayMessage> message = MessageBuilder.withPayload(payload).build();

        rocketMQTemplate.asyncSend(Demo03DelayMessage.TOPIC, message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送消息成功：{}",sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("异步发送消息失败。原因：{}",throwable.getMessage());
            }
        },30*1000L,delayLevel);
    }


}
