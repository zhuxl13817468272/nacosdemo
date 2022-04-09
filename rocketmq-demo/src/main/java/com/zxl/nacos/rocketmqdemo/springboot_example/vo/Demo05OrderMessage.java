package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo05OrderMessage {
    public static final String TOPIC = "DEMO_05";

    /**
     * 编号
     */
    private Integer id;
    private Integer orderId;
}
