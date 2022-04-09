package com.zxl.nacos.mongodbdemo.SeniorDemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: monitor
 * @description
 * @author: Zhu Xiaolong
 * @create: 2021-07-19 20:42
 **/
@Slf4j
@RestController
@RequestMapping("/mongodbDemo/")
public class MongodbSeniorDemoController {
    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 关联查询
     * 参考网址：https://www.cnblogs.com/yanghaolie/p/13164367.html、https://blog.csdn.net/laow1314/article/details/108649810
     *     官网：https://docs.mongodb.com/manual/reference/operator/aggregation/lookup/#examples
     * MongoDB 聚合框架（Aggregation Framework）是一个计算框架  从效果而言，聚合框架相当于 SQL 查询中的：GROUP BY　、 LEFT OUTER JOIN
     * 整个聚合运算过程称为管道（Pipeline），它是由多个步骤（Stage）组成的
     * 常见的stage步骤
     *    步骤	         作用	      SQL等价运算符
     *    $match         过滤          where
     *    $project       投影           as
     *    $sort          排序          order by
     *    $group         分组          group by
     *    $skip/$limit   结果限制      skip/limit
     *    $lookup        左外连接      left outer join
     */
    @GetMapping("testJoin")
    public void testJoin(){
        // 1. 数据查询
        Aggregation aggregation = Aggregation.newAggregation(
                //关联表
                Aggregation.lookup(
                        "JK_REPORTED_BACKHAND_SERVER_NODE",
                        "serverIp",
                        "serverIp",
                        "doc_member"
                ),
                //查询条件
                Aggregation.match(Criteria.where("isOk").is("1"))
                //分页
//                Aggregation.skip(Long.valueOf(pageNum)),
//                Aggregation.limit((long) pageSize),
                //排序
//                Aggregation.sort(Sort.Direction.DESC,"modifyTime")
        );

        List<StatusDataVo> resultList = new ArrayList<>();
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "JK_REPORTED_BACKHAND_SERVER_STATUS", Map.class); //如果封装嵌套实体类，则映射不了关联实体类
        List<Map> mappedResults = results.getMappedResults();

        // 2. 结果封装
        for(Map map:mappedResults){
            StatusDataVo reportedVo = new StatusDataVo();

            map.forEach((key,value)->{
                // 处理普通字段
                if(StringUtils.equalsIgnoreCase((String)key,"serverIp")){
                    System.out.println("serverIp的值为："+value);
                    reportedVo.setServerIp(String.valueOf(value));
                }
                // 处理关联字段
                if(StringUtils.equalsIgnoreCase((String)key,"doc_member")){
                    System.out.println("doc_member的值为："+value);
                    List<NodeDataVo> list = JSONObject.parseArray(JSON.toJSONString(value), NodeDataVo.class);
                    reportedVo.setListVo(list);
                    System.out.println(list);
                }
            });
            resultList.add(reportedVo);
        }
        System.out.println(resultList);

    }


    /**
     * 分组聚合查询  groupby\count\sum\max\min
     *  MongoDB 分组聚合查询     https://blog.csdn.net/qq_41970025/article/details/97173465
     *                          https://blog.csdn.net/baidu_38990811/article/details/80095495
     *
     *
     *db.JK_REPORTED_BACKHAND_SERVER_STATUS.aggregate([
     *    {
     * 		$match: {
     * 	    	'isOk': "1"
     *        }
     *    },
     *    {
     * 	    $group: {
     * 	        "_id": null,            _id为别名  null所有数据
     * 	        "total": {"$sum": "$_id"}    sum必须是数值型的才能得到聚合结果，如果是字符型的话，得到的聚合结果是0
     *        }
     *    },
     *    {
     * 	  $project: {
     * 	    "_id": 0,     project中为0的话，表示不在结果中显示该字段。1表示在结果中显示
     * 	    "total": 1
     *      }
     *    }
     *
     * ])
     *
     *
     * "_id": {
     * 	    "serverIp":"$serverIp",
     * 	     "sfzc":"$sfzc"
     * },
     */
    @RequestMapping("testGroupBy")
    public void testGroupBy (){
        // 1. 查询条件
        Aggregation aggregation = Aggregation.newAggregation(
                //查询条件
                Aggregation.match(Criteria.where("isOk").is("1")),

                //分组  根据serverIp分组              sum(_id)  _id字段求和   as别名sumId
                Aggregation.group("serverIp","sfzc").sum("_id").as("sumId"),

                /**
                 * 结果字段
                 *      多个字段分组{ "$project" : { "sumId" : 1, "serverIp" : "$_id.serverIp", "sfzc" : "$_id.sfzc" } }]     分组字段值赋给正常  GroupByVo(serverIp=10.228.130.125, sfzc=正常, _id=null, sumId=23)
                 *      单个字段分组 { "$project" : { "sumId" : 1, "serverIp" : "$_id.serverIp" }   注意：分组字段值会赋给_id，而不是分组字段      GroupByVo(serverIp=null, sfzc=null, _id=10.233.11.47, sumId=25)
                 */
                Aggregation.project("sumId").andInclude("serverIp").andInclude("sfzc")
        );

        // 2. 结果处理
//        AggregationResults<Map> aggregate = mongoTemplate.aggregate(aggregation, "JK_REPORTED_BACKHAND_SERVER_STATUS", Map.class);
//        List<Map> list = aggregate.getMappedResults();
        AggregationResults<GroupByVo> aggregate = mongoTemplate.aggregate(aggregation, "JK_REPORTED_BACKHAND_SERVER_STATUS", GroupByVo.class);
        List<GroupByVo> list = aggregate.getMappedResults();
        System.out.println(list);


        /*// 项目实战记录 -- 查询条件为：ALL("-1", "所有的") 和 WEITUO_TYPE("1", "委托"), excuteTime最大的一条记录，来获取最大时间
        Aggregation aggregation = Aggregation.newAggregation(
                // 查询条件封装
                Aggregation.match(Criteria.where("messageType").is(MongoDbMessageTypeEnums.WEITUO_TYPE.getType())
                        .andOperator(Criteria.where("isSuccess").is(MongoDbIsSuccessEnums.ALL.getType()), Criteria.where("isOk").is(IsOkEnums.OK.getType()), Criteria.where("excuteTime").gte(latestTradeDateOpenTime))),
                //分组
                Aggregation.group("exchangeId").sum("count").as("sumCount"),
                //结果字段
                Aggregation.project("sumCount").andInclude("exchangeId")
        );*/
    }


    /**
     * 事务管理（副本上的事务）
     *      参考文章：https://www.cnblogs.com/yanghaolie/p/13174435.html
     *          内存性数据库（NoSql）：数据库数据写入的顺序  数据在内存中先写日志文件（journal 中落地持久化日志文件），再写数据文件
     *          writeConcern 可以决定写操作到达多少个节点才算成功。
     *          journal 则定义如何才算成功，取值包括：true: 写操作落到 journal 文件中才算成功；false: 写操作到达内存即算作成功。
     *          readPreference 设置 分布式数据库从哪里读，primary: 只选择主节点；primaryPreferred：优先选择主节点，如果不可用则选择从节点； secondary：只选择从节点；secondaryPreferred：优先选择从节点，如果从节点不可用则选择主节点；nearest：选择最近的节点；
     *       ** readConcern 什么样的数据可以读，类似于关系数据库的隔离级别，available：读取所有可用的数据;local：读取所有可用且属于当前分片的数据;majority：读取在大多数节点上提交完成的数据;linearizable：可线性化读取文档;snapshot：读取最近快照中的数据;
     *          MongoDB 中的回滚
     *              • 写操作到达大多数节点之前都是不安全的，一旦主节点崩溃，而从节还没复制到该次操作，刚才的写操作就丢失了；
     *              • 把一次写操作视为一个事务，从事务的角度，可以认为事务被回滚了。所以从分布式系统的角度来看，事务的提交被提升到了分布式集群的多个节点级别的“提交”，而不再是单个节点上的“提交”。在可能发生回滚的前提下考虑脏读问题：
     *              • 如果在一次写操作到达大多数节点前读取了这个写操作，然后因为系统故障该操作回滚了，则发生了脏读问题；使用 {readConcern: “majority”} 可以有效避免脏读
     *          readConcern: snapshot（最高级别）
     *              {readConcern: “snapshot”} 只在多文档事务中生效。将一个事务的 readConcern设置为 snapshot，将保证在事务中的读：• 不出现脏读；• 不出现不可重复读；• 不出现幻读。因为所有的读都将使用同一个快照，直到事务提交为止该快照才被释放。
     *
     *     SpringBoot集成MongodbTemplate事务：https://blog.csdn.net/weixin_42322445/article/details/89358688、https://www.cnblogs.com/vettel0329/p/10778931.html
     *     mongodb多数据源之mongotemplate和事务的配置：https://blog.csdn.net/Winter_chen001/article/details/111868195
     *
     * 为什么Mongodb的事务是在副本（replica set）的基础上的？
     *      因为非关系数据库，主从角色是可以自动切换的，从角色可成为主角色（此时之前的主便成为从），所以事务也需要考虑副本的因素。
     *      而关系型数据库，比如mysql的从是指定主的ip和log日志文件的offset的，所以只需要考虑主mysql的事务就行了。
     *
     *
     */
    @RequestMapping("testTransaction")
    public void testTransaction(){
        /**
         * clientSession.startTransaction();
         *     collection.insertOne(clientSession, docOne);
         *     collection.insertOne(clientSession, docTwo);
         *     clientSession.commitTransaction();
         *     db.fsyncLock() 与db.fsyncUnlock() 可以锁定/解锁节点的写入，可以用来做事务代码的测试。
         */


    }

}
