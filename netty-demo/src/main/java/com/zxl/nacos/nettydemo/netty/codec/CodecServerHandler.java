package com.zxl.nacos.nettydemo.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  也可以将Protostuff编解码器做成一个编解码器用在ChannelPipeline中
 */
public class CodecServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        System.out.println("从客户端读取到String:" + msg.toString());
//        System.out.println("从客户端读取到Object:" + ((User)msg).toString());

        ByteBuf buf = (ByteBuf) msg;
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        System.out.println("从客户端读取到Object:" + ProtostuffUtil.deserializer(bytes,User.class));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
