package com.zxl.nacos.rocketmqdemo.springboot_example.vo;

import lombok.Data;

@Data
public class Demo02BatchMessage {
    public static final String TOPIC = "DEMO_02";

    /**
     * 编号
     */
    private Integer id;
}
