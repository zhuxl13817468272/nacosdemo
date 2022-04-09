package com.zxl.nacos.nettydemo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  非阻塞版本1.1 -- 体现了无锁串行化优势
 *      1. 不阻塞原因是因为有buffer缓冲区（在NIO中，所有数据都是用缓存区处理的。单线程通过channel从buffer读数据，也可以通过channel向buffer写数据） -- 线程自身读写不阻塞，线程间不阻塞
 *      2. 底层调用了linux内核函数；
 *  缺点：
 *      如果连接数太多的话，会有大量的无效遍历。假如有10000个连接，其中只有1000个连接有读写数据，而其他9000个连接并没有断开，每次轮询要遍历一万次，其中有十分之九的遍历都是无效的，这显然不是一个让人很满意的状态。
 */
public class NioServer {
    // 保存客户端连接
    static List<SocketChannel> clinetChannelList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        // 创建NIO ServerSocketChannel,与BIO的serverSocket类似
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞（线程之间不会阻塞）
        serverSocket.configureBlocking(false);
        System.out.println("服务启动成功");

        while(true){
            // 非阻塞模式accept方法不会阻塞，否则会阻塞
            // NIO的非阻塞是由操作系统内部实现的，底层调用了linux内核的accept函数
            SocketChannel clientSocket = serverSocket.accept();
            if(clientSocket != null){ // 如果有客户端进行连接
                System.out.println("连接成功");
                // 设置clientSocket为非阻塞（线程自身读写不会阻塞）
                clientSocket.configureBlocking(false);
                // 保存客户端连接在List中
                clinetChannelList.add(clientSocket);
            }

            // 遍历连接进行数据读取
            Iterator<SocketChannel> iterator = clinetChannelList.iterator();
            while (iterator.hasNext()){
                SocketChannel clinetSocket = iterator.next();
                ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                // 非阻塞模式read方法不会阻塞，否则会阻塞
                int len = clinetSocket.read(byteBuffer);
                // 如果有数据，把数据打印出来
                if (len > 0) {
                    System.out.println("接收到消息：" + new String(byteBuffer.array()));
                } else if (len == -1) { // 如果客户端断开，把socket从集合中去掉
                    iterator.remove();
                    System.out.println("客户端断开连接");
                }
            }
        }

    }

}
