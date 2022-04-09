package com.zxl.nacos.shardingspheredemo.algorithem;

import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Arrays;
import java.util.Collection;


public class MyRangeDSShardingAlgorithm implements RangeShardingAlgorithm<Long> {
    /**
     *  select * from m$->{1..2}.course where cid between 1 and 100;
     * @param availableTargetNames  可用的数据库名称 ，此例中对应m1 、m2
     * @param rangeShardingValue  sql传递的分片键的值（对应数据库分片的）
     * @return  返回经分片算法后的特定库名 ，此例中对应m1 、m2
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> rangeShardingValue) {
        for (String availableTargetName : availableTargetNames){
            System.out.println(" Range DataSource范围分片算法中availableTargetName为： " + availableTargetName);
        }
        Long lowerEndpoint = rangeShardingValue.getValueRange().lowerEndpoint();
        Long upperEndpoint = rangeShardingValue.getValueRange().upperEndpoint();
        String logicTableName = rangeShardingValue.getLogicTableName();
        System.out.println("Range DataSource范围分片算法中Range().lowerEndpoint()为： " + lowerEndpoint);
        System.out.println("Range DataSource范围分片算法中Range().upperEndpoint()为： " + upperEndpoint);
        System.out.println("Range DataSource范围分片算法中rangeShardingValue.getLogicTableName()为： " + logicTableName);

        // 分片算法逻辑

        return Arrays.asList("m1","m2");
    }


}
