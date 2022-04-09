package com.zxl.nacos.shardingspheredemo.algorithem;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Arrays;
import java.util.Collection;

public class MyHintTableShardingAlgorithm implements HintShardingAlgorithm<Long> {
    /**
     *  SELECT  cid,cname,user_id,cstatus  FROM course_2
     * @param availableTargetNames
     * @param hintShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, HintShardingValue<Long> hintShardingValue) {
        String key = hintShardingValue.getLogicTableName() + "_" + hintShardingValue.getValues().toArray()[0];
        System.out.println("hint table分片算法得到的：" + key);

        if(availableTargetNames.contains(key)){
            return Arrays.asList(key);
        }
        throw new UnsupportedOperationException("route "+ key +" is not supported ,please check your config");
    }
}
