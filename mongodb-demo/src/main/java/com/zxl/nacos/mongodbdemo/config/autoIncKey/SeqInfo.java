package com.zxl.nacos.mongodbdemo.config.autoIncKey;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 储存每个集合的ID记录
 */
@Document(collection = "sequence")
public class SeqInfo {
    @Id
    @Field("id")
    private String id; //主键
    @Field("seqId")
    private Long seqId; // 序列值
    @Field("collName")
    private String collName; // 集合名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getSeqId() {
        return seqId;
    }

    public void setSeqId(Long seqId) {
        this.seqId = seqId;
    }

    public String getCollName() {
        return collName;
    }

    public void setCollName(String collName) {
        this.collName = collName;
    }

    @Override
    public String toString() {
        return "SeqInfo{" +
                "id='" + id + '\'' +
                ", seqId=" + seqId +
                ", collName='" + collName + '\'' +
                '}';
    }
}
