kafka

    producer重要参数：
        retries_config     3
        retry_backoff_ms_config  100

        buffer_memory_config  32M
        batch_size_config    16k
        linger_ms_config   10 

    consumer重要参数 
        enable_auto_commit_config  false 

        auto_offset_reset_config  latest(默认)  earliest

        heartbeat_interval_ms_config   1000
        session_timeout_ms_config    10*1000
        max_poll_interval_ms_config  30*1000

        消费两种方式：queue和subscribe 
    
    broker重要概念
        kafka核心总控制器Controller (zookeeper选主、管理topic\partition\offset)
        
        消费者消费消息的offset记录机制   规则：hash(consumer group id) % __consumer_offsets  _consumer_offsets_xx(共50个)  key:consumerGroupId+topic+分区号  value:当前offset值 

        消费者rebalance机制  分配策略：range、round_robin、sticky 

    producer --> broker发布消息机制  规则：hash(key)%partitionNum   顺序写磁盘 

    consumer --> broker消费拉消息机制  规则：消费者rebalance机制

    日志分段存储  topic文件目录  topic名称+分区号
         日志示例：
            # 如果要定位消息的offset会先在这个文件里快速定位，再去log文件里找具体消息
            00000000000000000000.index
            # 消息存储文件，主要存offset和消息体
            00000000000000000000.log
            # 如果需要按照时间来定位消息的offset，会先在这个文件里查找
            00000000000000000000.timeindex

            00000000000005367851.index
            00000000000005367851.log
            00000000000005367851.timeindex

            00000000000009936472.index
            00000000000009936472.log
            00000000000009936472.timeindex


    线上问题及优化
        消息丢失情况
            发送端：acks机制 
            消费端：手动提交

        消息重复消费
            cause:发送端：重试机制  消费端：拉取消息后，没来及提交(offset)
            resolve:消费端做   幂等处理，redis或数据库标识字段对比

        消息顺序消费
            从发送端开始，将所有消息有序的发送到  同一分区 ，然后一  个消费者去消费  ，这种性能比较低。

        消息积压
            积压消息的一个消费端把消息  转发  到其他大partition多消费者的topic下（类似死信队列），来高能力消费

        消息延迟
            把消息按照不同延迟时间发送到指定的队列中（topic_30s,topic_30m,topic_1h...等）,然后通过定时器进行轮询消费

        消息回溯
            用consumer的offsetsForTimes、seek等方法指定某个offset偏移的消息开始消费
            
        kafka事务


        kafka高性能原因 ： 顺序读写、Zero Copy、Batch Data     https://mp.weixin.qq.com/s/UeRLaoJyL6WOmvNfy5wujQ