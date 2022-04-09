RocketMQ使用

    SpringBoot集成RocketMQ，消费者部分的核心就在这个@RocketMQMessageListener注解上。
        消息过滤可以由里面的selectorType属性和selectorExpression来定制   selectorType() default SelectorType.TAG;  selectorExpression() default "*"; eg :selectorExpression = "tag0 || tag1"
        消息有序消费还是并发消费则由consumeMode属性定制。   consumeMode() default ConsumeMode.CONCURRENTLY;  eg： consumeMode = ConsumeMode.ORDERLY
        消费者是集群部署还是广播部署由messageModel属性定制。  messageModel() default MessageModel.CLUSTERING;  eg: messageModel = MessageModel.BROADCASTING
        然后关于事务消息，还需要配置一个事务消息监听器：



    1. 顺序消息：
    发送端：默认采取Round Robin轮询方式把消息发送到不同的MessageQueue(分区队列)
    消费者：默认从多个MessageQueue上拉取消息，不保证顺序。Broker中一个队列内的消息是FIFO可以保证有序的


    发送者： syncSendOrderly(String destination, Object payload, String hashKey) 基于 hashKey 的哈希值取余，选择对应的队列
    消费者： consumeMode = ConsumeMode.ORDERLY MessageListenerOrderly对象要求RocketMQ内部通过锁队列的方式保证消息是一个队列一个队列来取的，即取完一个队列的消息后，再去取下一个队列的消息。


    2. 广播消息：
    默认为集群状态(MessageModel.CLUSTERING)，每一条消息只会被同一个消费组中的一个实例消费到（这跟kafka和rabbitMQ的集群模式是一样的）
    而广播模式则把消息发给了所有订阅了对应主题的消费者，而不管消费者是不是在同一个消费者组。


    3. 延迟消息
    开源版本的RocketMQ中，对延迟消息并不支持任意时间的延迟设定(商业版本中支持)，而是只支持18个固定的延迟级别，
    1到18分别对应messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h


    4. 批量消息
    批量消息是指将多条消息合并为一个批量消息，一次发送出去。这样做的好处是可以减少网络IO，提高吞吐量。一个批量消息最大限制为4M。


    5. 过滤消息
    TAG是RocketMQ中特有的一个消息属性。RocketMQ的最佳实践中就建议，使用RocketMQ时，一个应用可以就用一个Topic，而应用中的不同业务就用TAG来区分。

    destination = topic:tag 
    selectorExpression = "tag0 || tag1" 这句只订阅tag0和tag1的消息，可以使用SQL表达式来对消息进行过滤。

    6. 事物消息
    什么是事务消息。官网的介绍是：事务消息是在分布式系统中保证最终一致性的‘两阶段提交’的消息实现。他可以保证‘本地事务’执行与‘消息发送’两个操作的原子性，也就是这两个操作一起成功或者一起失败。
    对于复杂的分布式事务，RocketMQ提供的事务消息也是目前业内最佳的降级方案。

    我们来理解下事务消息的编程模型。事务消息只保证消息‘发送者的本地事务’与‘发消息’这两个操作的原子性，因此，事务消息的示例只涉及到消息发送者，对于消息消费者来说，并没有什么特别的。


    7.ACL权限控制
    RocketMQ提供Topic资源级别的用户访问控制。
