package com.zxl.nacos.mongodbdemo.SeniorDemo;

import lombok.Data;

import java.util.List;

/**
 * @program: monitor
 * @description
 * @author: Zhu Xiaolong
 * @create: 2021-07-20 09:18
 **/
@Data
public class StatusDataVo {

    private long id;
    private String writeTime;
    private String serverIp;
    private String type;
    private String role;
    private String dbCon;
    private String sfzc;
    private String cssl;
    private String hqqhcs;
    private String yhsl;
    private String zjs;
    private String sqs;
    private String dshs;
    private String zshs;
    private String sqny;
    private String dcf;
    private String dbd;
    private String dcl;
    private String sqlzx;

    private List<NodeDataVo> listVo;


//    @Data
//    public static class NodeDataVo {
//        private long id;
//        private String writeTime;
//        private String serverIp;
//        private String type;
//        private String ccsl;
//        private String ccStatus;
//    }

}
