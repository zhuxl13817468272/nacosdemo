package com.zxl.nacos.nettydemo.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 *  非阻塞版本1.2 -- 体现了多路复用，是netty源码的精髓所在
 *      1. 不阻塞原因是因为有buffer缓冲区（在NIO中，所有数据都是用缓存区处理的。单线程通过channel从buffer读数据，也可以通过channel向buffer写数据） -- 线程自身读写不阻塞，线程间不阻塞
 *      2. 特色是引入Selector多路复用器(通过jdk引入linux内核函数，由linux硬中断把有连接、读写操作的线程放入rdList用于遍历，除去了无效连接的遍历)
 *
 *  NIO有三大核心组件：  Channel(通道)， Buffer(缓冲区)，Selector(多路复用器)
 *      1、channel 类似于流，每个 channel 对应一个 buffer缓冲区，buffer 底层就是个数组
 *      2、channel 会注册到 selector 上，由 selector 根据 channel 读写事件的发生将其交由某个空闲的线程处理
 *      3、NIO 的 Buffer 和 channel 都是既可以读也可以写
 *
 *  缺点： 没有异步封装   -- 1. 这才引出了netty基于NIO异步非阻塞框架；2.AIO（NIO 2.0） 异步非阻塞，由于linux内核函数对异步支持不是太更力，netty尝试使用nio后最终放弃了。
 *
 */
public class NioSelectorServer {
    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocketChannel serverSocket = ServerSocketChannel.open(); // // 创建NIO ServerSocketChannel
        serverSocket.socket().bind(new InetSocketAddress(9000));
        // 设置ServerSocketChannel为非阻塞（线程间不阻塞）
        serverSocket.configureBlocking(false);
        Selector selector = Selector.open(); // @todo 通过jdk源码,引入linux内核函数int epoll_create(int size);
        // 把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        serverSocket.register(selector, SelectionKey.OP_ACCEPT); // @todo 通过jdk源码,引入linux内核函数int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
        System.out.println("服务启动成功");

        while(true){
            // 阻塞等待需要处理的事件发生
            selector.select(); //  @todo 通过jdk源码,引入linux内核函数int epoll_wait(int epfd, struct epoll_event *events, int maxevents, int timeout);

            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) { // // 遍历SelectionKey对事件进行处理
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) { // 如果是OP_ACCEPT事件，则进行连接获取和事件注册
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    //设置clientSocket为非阻塞（线程自身读写不会阻塞）
                    socketChannel.configureBlocking(false);
                    // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
                    socketChannel.register(selector,SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                }else if(key.isReadable()){  // 如果是OP_READ事件，则进行读取和打印
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128); //ByteBuffer是一个数组，但是难点在于读写指针需要切换
                    int len = socketChannel.read(byteBuffer);
                    // 如果有数据，把数据打印出来
                    if (len > 0) {
                        System.out.println("接收到消息：" + new String(byteBuffer.array()));
                    } else if (len == -1) { // 如果客户端断开连接，关闭Socket
                        System.out.println("客户端断开连接");
                        socketChannel.close();
                    }
                }

                //从事件集合里删除本次处理的key，防止下次select重复处理
                iterator.remove();
            }
        }
    }
}
