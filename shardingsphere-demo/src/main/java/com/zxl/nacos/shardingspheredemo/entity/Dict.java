package com.zxl.nacos.shardingspheredemo.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("t_dict") //数据库中表名和类名不一致时，需要用@TableName映射
public class Dict {
    private Long dictId;
    private String ustatus;
    private String uvalue;

    public Long getDictId() {
        return dictId;
    }

    public void setDictId(Long dictId) {
        this.dictId = dictId;
    }

    public String getUstatus() {
        return ustatus;
    }

    public void setUstatus(String ustatus) {
        this.ustatus = ustatus;
    }

    public String getUvalue() {
        return uvalue;
    }

    public void setUvalue(String uvalue) {
        this.uvalue = uvalue;
    }

    @Override
    public String toString() {
        return "Dict{" +
                "dictId=" + dictId +
                ", ustatus='" + ustatus + '\'' +
                ", uvalue='" + uvalue + '\'' +
                '}';
    }
}
