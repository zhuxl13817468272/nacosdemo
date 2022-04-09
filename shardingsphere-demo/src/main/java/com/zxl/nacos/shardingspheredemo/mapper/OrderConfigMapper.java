package com.zxl.nacos.shardingspheredemo.mapper;

import com.zxl.nacos.shardingspheredemo.entity.OrderConfigDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderConfigMapper {
    OrderConfigDO selectByPayTimeout(@Param("payTimeout") Integer payTimeout);

    void insert(OrderConfigDO orderConfigDO);
}
