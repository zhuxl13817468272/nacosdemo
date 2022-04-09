package com.zxl.nacos.nettydemo.netty.chat;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) throws Exception{
        //客户端需要一个事件循环组
        EventLoopGroup group = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();  //注意客户端使用的不是 ServerBootstrap 而是 Bootstrap
            bootstrap.group(group) //设置线程组
                    .channel(NioSocketChannel.class)  // 使用 NioSocketChannel 作为客户端的通道实现
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(8192, Unpooled.copiedBuffer("\n".getBytes())));
                            socketChannel.pipeline().addLast("decode",new StringDecoder());
                            socketChannel.pipeline().addLast("encode",new StringEncoder());
                            socketChannel.pipeline().addLast(new ChatClientHandler());
                        }
                    });
            System.out.println("netty client start");

            //启动客户端去连接服务器端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 9000).sync();

            // 发送数据
            Channel channel = channelFuture.channel();
            System.out.println("========" + channel.localAddress() + "========");
            //客户端需要输入信息， 创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                //通过 channel 发送到服务器端   一定要加"\n",即使发出去了，服务端也接受不到，因为服务端没法入站解码（DelimiterBasedFrameDecoder(8192, Unpooled.copiedBuffer("\n".getBytes()))）
                channel.writeAndFlush(msg+ "\n");
            }
//            for (int i = 0; i < 200; i++) {
//                channel.writeAndFlush("hello，诸葛!" + "\n");
//            }

            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();

        }finally {
            group.shutdownGracefully();
        }
    }
}
