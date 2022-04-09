package com.zxl.nacos.rabbitmqdemo.producer;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HelloSender {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send(){
        String context = "hello " + new Date();
        System.out.println("sender : " + context);

        this.rabbitTemplate.convertAndSend("hello",context);
    }

}
