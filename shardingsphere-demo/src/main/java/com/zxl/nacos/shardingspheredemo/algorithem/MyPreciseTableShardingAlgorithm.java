package com.zxl.nacos.shardingspheredemo.algorithem;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigInteger;
import java.util.Collection;

public class MyPreciseTableShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    /**
     *   select * from mX.course_$->{1..2} where cid = ? or cid in (?,?)
     * @param availableTargetNames 可用的同一库下表名 ，此例中对应course_1 、course_2
     * @param preciseShardingValue sql传递的分片键的值（对应数据库分片的）
     * @return 返回经分片算法后的特定表名 ，此例中对应course_1 、course_2
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        for (String availableTargetName : availableTargetNames){
            System.out.println(" Precise Table精确分片算法中availableTargetName为： " + availableTargetName);
        }
        String logicTableName = preciseShardingValue.getLogicTableName();
        String shardingColumnName = preciseShardingValue.getColumnName();
        Long shardingColumnValue = preciseShardingValue.getValue();
        System.out.println(" Precise Table精确分片算法中preciseShardingValue.getLogicTableName为： " + logicTableName);
        System.out.println(" Precise Table精确分片算法中preciseShardingValue.getColumnName： " + shardingColumnName);
        System.out.println(" Precise Table精确分片算法中preciseShardingValue.getValue： " + shardingColumnValue);

        //  实现表分片算法： course_$->{cid%2+1)
        BigInteger shardingColumnValueB = BigInteger.valueOf(shardingColumnValue);
        BigInteger resB = (shardingColumnValueB.mod(new BigInteger("2"))).add(new BigInteger("1"));
        String key = logicTableName+"_"+resB;
        System.out.println(" Precise Table精确经过分片算法后的特定表名： " + key);
        if(availableTargetNames.contains(key)){
            return key;
        }else {
            throw new UnsupportedOperationException("route "+ key +" is not supported ,please check your config");
        }
    }
}
