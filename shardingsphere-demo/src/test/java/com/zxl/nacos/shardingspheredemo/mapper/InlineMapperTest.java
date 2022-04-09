package com.zxl.nacos.shardingspheredemo.mapper;

import com.zxl.nacos.shardingspheredemo.ShardingsphereDemoApplication;
import com.zxl.nacos.shardingspheredemo.entity.OrderConfigDO;
import com.zxl.nacos.shardingspheredemo.entity.OrderDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShardingsphereDemoApplication.class)
public class InlineMapperTest {

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Test
    public void testInsert() {
        OrderConfigDO order = new OrderConfigDO();
        order.setPayTimeout(3);
        orderConfigMapper.insert(order);
    }

    @Test
    public void testSelectById(){
        OrderConfigDO orderConfigDO = orderConfigMapper.selectByPayTimeout(10);
        System.out.println("orderConfigMapper.selectById请求成功：" + orderConfigDO);
    }


    @Autowired
    private OrderMapper orderMapper;

    @Test
    public void testSelectById1() {
        OrderDO order = orderMapper.selectById(3);
        System.out.println(order);
    }

    @Test
    public void testSelectListByUserId() {
        for(int i = 1; i<=4;i++) {
            List<OrderDO> orders = orderMapper.selectListByUserId(i);
            orders.forEach(System.out::println);
        }
    }

    @Test
    public void testInsert1() {
        for(int i = 5; i<=7;i++) {
            OrderDO order = new OrderDO();
            order.setUserId(i);
            orderMapper.insert(order);
        }
    }

}