package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo03DelayMessage {
    public static final String TOPIC = "DEMO_03";

    /**
     * 编号
     */
    private Integer id;
}
