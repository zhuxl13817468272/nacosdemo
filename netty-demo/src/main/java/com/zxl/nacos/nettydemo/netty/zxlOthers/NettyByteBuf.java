package com.zxl.nacos.nettydemo.netty.zxlOthers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.CharsetUtil;

/**
 *    JDK的ByteBuffer 与 Netty的ByteBuf的区别
 *          java.nio.ByteBuffer: 有一个指针用于处理读写操作，每次读写的时候都需要调用flip()(读写模式切换)或是clear()方法，不然将会报异常。
 *          io.netty.buffer.ByteBuf: 通过两个指针协助缓存区的读写操作，分别为 readIndex 和 writerIndex 。
 *
 *    ByteBuf通过readerindex和writerIndex和capacity，将buffer分成三个区域
 *         已经读取的区域：[0,readerindex)
 *         可读取的区域：[readerindex,writerIndex)
 *         可写的区域: [writerIndex,capacity)
 *
 *    ByteBuf扩容机制
 *          minNewCapacity:表用户需要写入的值大小
 *          threshold:阈值，为ByteBuf内部设定容量的最大值
 *          maxCapacity:Netty最大能接受的容量大小，一般为int的最大值
 *         值大小由小到大                  64Byte的倍数增加         4M             +4M增加                            throw IllegalArgumentException
 *          --------->minNewCapacity----------------------------->threshold---------------------------->maxCapacity--------------------------------->
 *
 */
public class NettyByteBuf {
    public static void main(String[] args) {
        // 参考网址： https://my.oschina.net/ywbrj042/blog/902321
        //无参数的工厂方法，还可以，调用buffer(int initialCapacity, int maxCapacity) 来指定初始化和最大容量限制。
        ByteBuf byteBuf1 = UnpooledByteBufAllocator.DEFAULT.buffer();

        //写入一个byte。
        byteBuf1.writeByte(3);

        //读取一个字节。
        System.out.println("get value "+(byteBuf1.readByte())+" from buffer");

        try {
            //再读取一个字节，超过了读取限制，则抛出异常。
            System.out.println("get value " + (byteBuf1.readByte()) + " from buffer");
        }catch (Exception e){
            e.printStackTrace();
        }

        //清空，索引值均恢复0.
        byteBuf1.clear();

        //循环写入257个字节，超过了默认初始化容量256的限制，则会触发扩容操作。
        for (int i=0; i<257; i++){
            byteBuf1.writeByte(3);
        }
        System.out.println("byteBuf1 = " + byteBuf1);


        // 创建一个字节数组byte[10]的对象
        ByteBuf byteBuf2 = Unpooled.buffer(10);
        System.out.println("byteBuf2 = " + byteBuf2);

        for(int i = 0;i < 8; i++){
            byteBuf2.writeByte(i);
        }
        System.out.println("byteBuf2 = " + byteBuf2);

        for(int i = 0;i < 5; i++){
            System.out.println(byteBuf2.getByte(i));
        }
        System.out.println("byteBuf2 = " + byteBuf2);

        for(int i = 0;i < 5; i++){
            System.out.println(byteBuf2.readByte());
        }
        System.out.println("byteBuf2 = " + byteBuf2);


        ByteBuf byteBuf3 = Unpooled.copiedBuffer("hell0,zxl", CharsetUtil.UTF_8);
        if(byteBuf3.hasArray()){
            byte[] content = byteBuf3.array();
            System.out.println(new String(content,CharsetUtil.UTF_8));
            System.out.println("byteBuf3 = " + byteBuf3);

            System.out.println(byteBuf3.getByte(0)); // 获取数组0这个位置的字符h的ascii码，h=104

            int len = byteBuf3.readableBytes();
            System.out.println("len = " + len);

            for(int i = 0; i < len; i++){
                System.out.println((char)byteBuf3.getByte(i));
            }

            System.out.println(byteBuf3.getCharSequence(0,6,CharsetUtil.UTF_8));
            System.out.println(byteBuf3.getCharSequence(6,6,CharsetUtil.UTF_8));
        }

    }
}
