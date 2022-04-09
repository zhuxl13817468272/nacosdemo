package com.zxl.nacos.jvmdemo.jvmClassLoad;

public class A {
    static {
        System.out.println("*************load A************");
    }

    public A() {
        System.out.println("*************initial A************");
    }

}
