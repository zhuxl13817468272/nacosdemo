package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo04BroadcastingMessage {
    public static final String TOPIC = "DEMO_04";

    /**
     * 编号
     */
    private Integer id;
}
