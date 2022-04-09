package com.zxl.nacos.nettydemo.netty.splitPacket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class MyMessageDecoder extends ByteToMessageDecoder {

    int length = 0;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        System.out.println();
        System.out.println("MyMessageDecoder decode 被调用");
        //需要将得到二进制字节码-> MyMessageProtocol 数据包(对象)
        System.out.println(in);

        if(in.readableBytes() >= 4) {
            if (length == 0){
                length = in.readInt();//  24   "你好，我是张三！" 为 24个字节
            }
            /**
             *  PooledUnsafeDirectByteBuf(ridx: 1008, widx: 1024, cap: 1024)   // in.readableBytes() = 1024 -1008 = 16 < 24
             *   当前可读数据不够，继续等待。。
             *  PooledUnsafeDirectByteBuf(ridx: 1012, widx: 1024, cap: 1024)  // in.readableBytes() = 1024 -1012 = 12 < 24
             *   当前可读数据不够，继续等待。。
             *  PooledUnsafeDirectByteBuf(ridx: 1012, widx: 5096, cap: 8192)
             */
            if (in.readableBytes() < length) {
                System.out.println("当前可读数据不够，继续等待。。");
                return;
            }

            byte[] content = new byte[length];
            if (in.readableBytes() >= length){
                in.readBytes(content);

                //封装成MyMessageProtocol对象，传递到下一个handler业务处理
                MyMessageProtocol messageProtocol = new MyMessageProtocol();
                messageProtocol.setLen(length); // 24  24个字节
                messageProtocol.setContent(content); // byte[24]  24个字节的内容
                out.add(messageProtocol);
            }
            length = 0;
        }
    }
}
