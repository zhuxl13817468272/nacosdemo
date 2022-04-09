package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo01TagMessage {
    public static final String TOPIC = "DEMO_01";

    /**
     * 编号
     */
    private Integer id;
}
