package com.zxl.nacos.rabbitmqdemo.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class Person {
    private String name;
    private Integer age;
    private String address;
}
