package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo06TransactionMessage {
    public static final String TOPIC = "DEMO_06";

    /**
     * 编号
     */
    private Integer id;
}
