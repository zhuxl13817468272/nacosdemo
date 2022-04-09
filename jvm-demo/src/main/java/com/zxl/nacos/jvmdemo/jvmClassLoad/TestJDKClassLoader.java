package com.zxl.nacos.jvmdemo.jvmClassLoad;

import sun.misc.Launcher;

import java.net.URL;

/**
 * 双亲委派机制
 *      引导类加载器ClassLoader
 *      扩展类加载器ExtClassLoader
 *      应用程序类加载器AppClassLoader
 *      自定义加载器
 * 为什么要设计双亲委派机制？
 *      1.沙箱安全机制：自己写的java.lang.String.class类不会被加载，这样便可以防止核心API库被随意篡改
 *      2.避免类的重复加载：当父类已经加载了该类时，就没有必要子classloader在加载一次，保证被加载类的唯一性
 */
public class TestJDKClassLoader {
    public static void main(String[] args) {
        System.out.println(String.class.getClassLoader());//null(bootstrapLoader) 该类在jdk的/jre/lib/rt.jar里面
        System.out.println(com.sun.crypto.provider.DESKeyFactory.class.getClassLoader().getClass().getName());//extClassLoader 该类在jdk的/jre/lib/ext/sunjce_provider.jar里面
        System.out.println(TestJDKClassLoader.class.getClassLoader().getClass().getName());//appClassLoader(systemClassLoader)

        // appClassLoader --> extClassLoader --> bootstrapLoader
        System.out.println();
        ClassLoader appClassLoader = ClassLoader.getSystemClassLoader();
        ClassLoader extClassloader = appClassLoader.getParent();
        ClassLoader bootstrapLoader = extClassloader.getParent();
        System.out.println("the bootstrapLoader : " + bootstrapLoader);
        System.out.println("the extClassloader : " + extClassloader);
        System.out.println("the appClassLoader : " + appClassLoader);

        System.out.println();
        System.out.println("bootstrapLoader加载以下文件：");
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        for (int i = 0; i < urls.length; i++) {
            System.out.println(urls[i]);
        }

        System.out.println();
        System.out.println("extClassloader加载以下文件：");
        System.out.println(System.getProperty("java.ext.dirs"));

        System.out.println();
        System.out.println("appClassLoader加载以下文件：");
        System.out.println(System.getProperty("java.class.path"));

    }

}
