package com.zxl.nacos.shardingspheredemo.algorithem;

import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.math.BigInteger;
import java.util.Collection;


public class MyPreciseDSShardingAlgorithm implements PreciseShardingAlgorithm<Long> {
    /**
     *   select * from m$->{1..2}.course where cid = ? or cid in (?,?)  走由分片键经过分片算法后的指定库表
     * @param availableTargetNames  可用的数据库名称 ，此例中对应m1 、m2
     * @param preciseShardingValue  sql传递的分片键的值（对应数据库分片的）
     * @return   返回经分片算法后的特定库名 ，此例中对应m1 、m2
     */
    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> preciseShardingValue) {
        for (String availableTargetName : availableTargetNames){
            System.out.println(" Precise DataSource精确分片算法中availableTargetName为： " + availableTargetName);
        }
        String logicTableName = preciseShardingValue.getLogicTableName();
        String shardingColumnName = preciseShardingValue.getColumnName();
        Long shardingColumnValue = preciseShardingValue.getValue();
        System.out.println(" Precise DataSource精确分片算法中preciseShardingValue.getLogicTableName为： " + logicTableName);
        System.out.println(" Precise DataSource精确分片算法中preciseShardingValue.getColumnName： " + shardingColumnName);
        System.out.println(" Precise DataSource精确分片算法中preciseShardingValue.getValue： " + shardingColumnValue);


        //  实现库分片算法： m$->{cid%2+1)
        BigInteger shardingColumnValueB = BigInteger.valueOf(shardingColumnValue);
        BigInteger resB = (shardingColumnValueB.mod(new BigInteger("2"))).add(new BigInteger("1"));
        String key = "m" + resB;
        System.out.println(" Precise DataSource精确经过分片算法后的特定库名： " + key);

        if(availableTargetNames.contains(key)){
            return key;
        }else {
            throw new UnsupportedOperationException("route "+ key +" is not supported ,please check your config");
        }
    }
}
