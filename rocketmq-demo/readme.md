RocketMQ单机搭建

    RocketMQ 
        静默启动NameServer:       nohup bin/mqadminsrv & (没有)   nohup bin/mqnamesrv &
        静默启动runbroker.sh :    nohup ./bin/mqbroker &

        
        命令行快速验证： 
            首先需要配置一个环境变量NAMESRV_ADDR指向我们启动的NameServer服务：  export NAMESRV_ADDR='localhost:9876'
            然后启动消息生产者发送消息：默认会发1000条消息：  bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
            然后启动消息消费者接收消息：  bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer

        
        # 1.关闭NameServer
                sh bin/mqshutdown namesrv
        # 2.关闭Broker
                sh bin/mqshutdown broker
    

        使用jps指令，能看到一个NameSrvStartup进程和两个BrokerStartup进程。
        nohup.out中也有启动成功的日志
            对应的日志文件：
                # 查看nameServer日志
                    tail -500f ~/logs/rocketmqlogs/namesrv.log
                # 查看broker日志
                    tail -500f ~/logs/rocketmqlogs/broker.log
        

    搭建管理控制台
        打包启动： 
            cmd进入  D:\tools\rocketmq\install\rocketmq-externals-master\rocketmq-console 目录，        执行 mvn clean package -Dmaven.test.skip=true
                    D:\tools\rocketmq\install\rocketmq-externals-master\rocketmq-console\target目录    运行 java -jar rocketmq-console-ng-2.0.0.jar 
        访问： 浏览器中输入‘127.0.0.1：配置端口’，成功后即可查看。
