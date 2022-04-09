package com.zxl.nacos.shardingspheredemo.mapper;

import com.zxl.nacos.shardingspheredemo.entity.OrderDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMapper {
    OrderDO selectById(@Param("id") Integer id);

    List<OrderDO> selectListByUserId(@Param("userId") Integer userId);

    void insert(OrderDO orderDO);
}
