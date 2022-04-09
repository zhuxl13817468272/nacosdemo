package com.zxl.nacos.jvmdemo.jvmClassLoad;

import com.zxl.nacos.jvmdemo.jvmClassLoad.A;
import com.zxl.nacos.jvmdemo.jvmClassLoad.B;

public class TestDynamicLoad {
    static {
        System.out.println("*************load TestDynamicLoad************");
    }
    public static void main(String[] args) {
        new A();
        System.out.println("*************load test************");
        B b = null;  //B不会加载，除非这里执行 new B()
    }
}
