package com.zxl.nacos.jvmdemo.jvmClassLoad;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *  -Xms10M -Xmx10M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\jvm.dump
 *  生成D:\jvm.dump文件，并导入jvisualvm分析
 */
public class OOMTest {

    public static List<Object> list = new ArrayList<>();

    // JVM设置    
    // -Xms10M -Xmx10M -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:\jvm.dump
    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        int i = 0;
        int j = 0;
        while (true) {
            list.add(new User(i++, UUID.randomUUID().toString()));
            new User(j--, UUID.randomUUID().toString());
        }
    }
}