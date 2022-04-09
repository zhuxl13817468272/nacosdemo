package com.zxl.nacos.shardingspheredemo.algorithem;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyComplexDSShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {
    /**
     *  SELECT  cid,cname,user_id,cstatus  FROM  m$->{1..2}.course  WHERE  cid BETWEEN ? AND ? AND user_id = ?
     * @param availableTargetNames
     * @param complexKeysShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> complexKeysShardingValue) {
        String logicTableName = complexKeysShardingValue.getLogicTableName();
        Range<Long> cidRangeValues = complexKeysShardingValue.getColumnNameAndRangeValuesMap().get("cid");
        Collection<Long> userIdShardingValues = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get("user_id");
        Long upperEndpoint = cidRangeValues.upperEndpoint();
        Long lowerEndpoint = cidRangeValues.lowerEndpoint();


        List<String> res = new ArrayList<>();
        for(Long userId : userIdShardingValues){
            //m$->{userID%2+1}
            BigInteger target = (BigInteger.valueOf(userId)).mod(new BigInteger("2")).add(new BigInteger("1"));
            res.add("m"+target);
            System.out.println(" Complex DataSource经过分片算法后的特定库名： " + "m"+target);
        }
        return res;
    }
}
