package com.zxl.nacos.nettydemo.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  也可以将Protostuff编解码器做成一个编解码器用在ChannelPipeline中
 */
public class CodecClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("MyClientHandler发送数据");
//        ctx.writeAndFlush("测试String编解码");
//        ctx.writeAndFlush(new User(1,"zhuge"));

        ByteBuf buf = Unpooled.copiedBuffer(ProtostuffUtil.serializer(new User(1, "zhuge")));
        ctx.writeAndFlush(buf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到服务器消息：" + msg);
    }
}
