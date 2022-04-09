package com.zxl.nacos.shardingspheredemo.algorithem;

import com.google.common.collect.Range;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyComplexTableShardingAlgorithm implements ComplexKeysShardingAlgorithm<Long> {
    /**
     *  SELECT  cid,cname,user_id,cstatus  FROM  mX.course_$->{1..2}  WHERE  cid BETWEEN ? AND ? AND user_id = ?
     * @param availableTargetNames
     * @param complexKeysShardingValue
     * @return
     */
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> complexKeysShardingValue) {
        String logicTableName = complexKeysShardingValue.getLogicTableName();
        Range<Long> cidRangeValues = complexKeysShardingValue.getColumnNameAndRangeValuesMap().get("cid");
        Collection<Long> userIdShardingValues = complexKeysShardingValue.getColumnNameAndShardingValuesMap().get("user_id");

        List<String> res = new ArrayList<>();
        for(Long userId:userIdShardingValues){
            //course_$->{userID%2+1}
            BigInteger target = (BigInteger.valueOf(userId)).mod(new BigInteger("2")).add(new BigInteger("1"));
            res.add(logicTableName+"_"+target);
            System.out.println(" Complex Table经过分片算法后的特定表名： " +logicTableName+"_"+target);
        }
        return res;
    }
}
