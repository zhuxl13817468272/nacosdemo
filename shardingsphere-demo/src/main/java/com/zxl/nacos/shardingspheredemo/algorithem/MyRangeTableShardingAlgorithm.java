package com.zxl.nacos.shardingspheredemo.algorithem;

import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;

import java.util.Arrays;
import java.util.Collection;

public class MyRangeTableShardingAlgorithm implements RangeShardingAlgorithm<Long> {
    /**
     *   select * from m$->{1..2}.course where cid between 1 and 100;
     * @param availableTargetNames
     * @param rangeShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<Long> rangeShardingValue) {
        for (String availableTargetName : availableTargetNames){
            System.out.println(" Range Table范围分片算法中availableTargetName为： " + availableTargetName);
        }
        Long lowerEndpoint = rangeShardingValue.getValueRange().lowerEndpoint();
        Long upperEndpoint = rangeShardingValue.getValueRange().upperEndpoint();
        String logicTableName = rangeShardingValue.getLogicTableName();
        System.out.println("Range Table范围分片算法中Range().lowerEndpoint()为： " + lowerEndpoint);
        System.out.println("Range Table范围分片算法中Range().upperEndpoint()为： " + upperEndpoint);
        System.out.println("Range Table范围分片算法中rangeShardingValue.getLogicTableName()为： " + logicTableName);

        // 分片算法逻辑

        return Arrays.asList(logicTableName+"_1",logicTableName+"_2");
    }
}
