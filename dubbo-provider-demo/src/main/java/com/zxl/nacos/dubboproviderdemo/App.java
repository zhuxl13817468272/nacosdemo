package com.zxl.nacos.dubboproviderdemo;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class App {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("dubbo/provider.xml");
        context.start();
        System.in.read();
    }
}
