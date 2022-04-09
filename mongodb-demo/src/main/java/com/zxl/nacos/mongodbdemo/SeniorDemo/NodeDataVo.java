package com.zxl.nacos.mongodbdemo.SeniorDemo;

import lombok.Data;

/**
 * @program: monitor
 * @description
 * @author: Zhu Xiaolong
 * @create: 2021-07-20 20:52
 **/
@Data
public class NodeDataVo {
    private long id;
    private String writeTime;
    private String serverIp;
    private String type;
    private String ccsl;
    private String ccStatus;
}
