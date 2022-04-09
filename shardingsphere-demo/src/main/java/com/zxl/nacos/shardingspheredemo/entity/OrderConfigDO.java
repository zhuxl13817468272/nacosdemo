package com.zxl.nacos.shardingspheredemo.entity;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString
@Accessors(chain = true)
public class OrderConfigDO {
    private Long id;
    private Integer payTimeout;
}
