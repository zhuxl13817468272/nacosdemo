package com.zxl.nacos.nettydemo.netty.zxlOthers.directbuffer;

import java.nio.ByteBuffer;

/**
 * 直接内存与堆内存的区别
 *      1. 直接内存分配在物理内存，堆内存在JVM堆上分配内存
 *      2. 与在JVM堆分配内存(allocate)相比，直接内存分配（allocateDirect）的访问性能更好，但分配较慢。
 *
 *  零拷贝                                                               server
 *                               -------------------------------------------------------------------------------------------
 *                               |                                                                                          |
 *      --------                 |        read       ---------------            ----------------           -------------    |
 *     |        | -http三次握手---|网卡--> accept--- | established   |  -------->|               | -------->|            |   |
 *     | client |                |        write     | socket buffer |     copy  | direct buffer |   ？？？  |   jvm堆    |   |
 *     |        | <---- http ----|网卡 <----------- | 内核缓冲区     | <-------- |   物理缓冲区   | <--------| 内存缓存区  |   |
 *     ---------                 |                  ----------------            ----------------           -------------    |
 *                               |                    linux                    netty/tomcat(epoll)         webapp(web.xml)  |
 *                               -------------------------------------------------------------------------------------------
 *    如果jvm堆内存（用户态）引入directBuffer（内核态），则为零拷贝（数据不需要再次拷贝到jvm堆内存）
 *    如果jvm堆内存（用户态）需要数据从directBuffer（内核态）拷贝，然后更新数据在拷贝到directBuffer（内核态），则为正常NIO操作，不为零拷贝。
 */
public class DirectMemoryTest {

    public static void heapAccess() {
        long startTime = System.currentTimeMillis();
        //分配堆内存
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();

            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }
            buffer.clear();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("堆内存访问:" + (endTime - startTime) + "ms");
    }

    public static void directAccess() {
        long startTime = System.currentTimeMillis();
        //分配直接内存
        ByteBuffer buffer = ByteBuffer.allocateDirect(1000);
        for (int i = 0; i < 100000; i++) {
            for (int j = 0; j < 200; j++) {
                buffer.putInt(j);
            }
            buffer.flip();
            for (int j = 0; j < 200; j++) {
                buffer.getInt();
            }
            buffer.clear();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("直接内存访问:" + (endTime - startTime) + "ms");
    }

    public static void heapAllocate() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            ByteBuffer.allocate(100);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("堆内存申请:" + (endTime - startTime) + "ms");
    }

    public static void directAllocate() {
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            ByteBuffer.allocateDirect(100);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("直接内存申请:" + (endTime - startTime) + "ms");
    }

    public static void main(String args[]) {
        for (int i = 0; i < 10; i++) {
            heapAccess();
            directAccess();
        }

        System.out.println();

        for (int i = 0; i < 10; i++) {
            heapAllocate();
            directAllocate();
        }
    }
}