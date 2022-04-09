package com.zxl.nacos.rabbitmqdemo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc//测试接口用
class RabbitmqDemoApplicationTests {

    @Before
    public void testBefore(){
        System.out.println("测试前");
    }

    @After
    public void testAfter(){
        System.out.println("测试后");
    }

    @Test
    public void contextLoads() {
    }



    @Autowired
    public AmqpTemplate rabbitTemplate;

    @Test
    public void send(){
        rabbitTemplate.convertAndSend("myQueue","now"+new Date());
    }

    @Test
    public void sendOrder(){
        rabbitTemplate.convertAndSend("myOrder","computer","now" +new Date());
    }

}
