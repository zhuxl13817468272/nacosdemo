package com.zxl.nacos.nettydemo.netty.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {
    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();

        // 通知其他channel，新channel进来了
        channels.forEach(channel -> {
            if(channel != inComing) {
                channel.writeAndFlush("[欢迎: " + inComing.remoteAddress() + "] 进入聊天室！\n");
            }
        });

        channels.add(inComing);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel outComing = ctx.channel();

        // 通知其他channel，有channel出去了
        channels.forEach(channel -> {
            if(channel != outComing) {
                channel.writeAndFlush("[再见: ]" + outComing.remoteAddress() +" 离开聊天室！\n");
            }
        });

        channels.remove(outComing);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String msg) throws Exception {
        Channel inComing = channelHandlerContext.channel();

        channels.forEach(channel -> {
            if(channel != inComing){
                channel.writeAndFlush("[ 用户 ]" + inComing.remoteAddress() + " 发送了消息：]" + msg + "\n");
            }else {
                channel.writeAndFlush("[ 自己 ]发送了消息：" + msg + "\n");
            }
        });

    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 在线");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println("[" + inComing.remoteAddress() + "]: 离线");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel inComing = ctx.channel();
        System.out.println(inComing.remoteAddress() + "通讯异常！");
        inComing.close();
    }
}
