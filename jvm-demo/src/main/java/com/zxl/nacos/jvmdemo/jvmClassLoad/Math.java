package com.zxl.nacos.jvmdemo.jvmClassLoad;

/**
 * 01 - JVM类加载器机制以及tomcat类加载机制
 * java.exe()调用jvm.dll --> java虚拟机
 * 引导类加载器实例
 *
 * c++调用java代码创建jvm启动器实例sun.misc.Launcher,该类由引导类加载器负责加载创建其他类加载器
 *
 * 引导类加载器实例 -- > jvm启动器实例sun.misc.Launcher --> 创建其他类加载器
 * sun.misc.Launcher.getLauncher()  launcher.getClassLoader()  classLoader.loadClass("com.tuling.jvm.Math")  Math.main()  JVM销毁
 *
 * 双亲委派机制
 *      引导类加载器ClassLoader
 *      扩展类加载器ExtClassLoader
 *      应用程序类加载器AppClassLoader
 *      自定义加载器
 * 为什么要设计双亲委派机制？
 *      1.沙箱安全机制：自己写的java.lang.String.class类不会被加载，这样便可以防止核心API库被随意篡改
 *      2.避免类的重复加载：当父类已经加载了该类时，就没有必要子classloader在加载一次，保证被加载类的唯一性
 *
 *  Tomcat打破双亲委派机制
 *    我们思考一下：Tomcat是个web容器， 那么它要解决什么问题：
 *      一个web容器可能部署两个应用程序，不同的应用程序可能会依赖同一个第三方类库的不同版本，怎么解决？如果使用双亲委派模式会产生什么问题？
 *      部署在 同一个web容器中相同的类库相同的版本可以共享的。
 *      web容器也有自己依赖的类库，不能与应用程序的类库混淆。
 *      web容器要支持jsp文件的热加载（jsp文件其实也就是class文件）。每个jsp文件对应一个唯一的类加载器，当一个jsp文件修改了，就直接卸载这个jsp类加载器。重新创建类加载器，重新加载jsp文件。
 *  Tomcat的几个主要类加载器
 *      webappClassLoader:只对当前Webapp可见
 *      catalinaLoader:tomcat容器私有的类加载器，对webapp不可见
 *      sharedLoader:对于所有webapp可见，但对于tomcat容器不可见
 *      commonLoader:可以被tomcat容器本身以及各个webapp访问
 *      jasperLoader:加载范围仅仅是这个jsp文件所编译出来的一个.class文件
 *拓展：
 *  在运行中的jvm，jsp更换可以生效、class字节码文件更换能生效吗？（应该不能）、.yml文件更换能生效吗？（应该能）
 *
 *
 * 02 - JVM内存模型及优化
 *
 * 03 - JVM对象创建与内存分配机制
 * *拓展：
 *  *  在运行中的jvm，jsp更换可以生效、class字节码文件更换能生效吗？（应该不能）、.yml文件更换能生效吗？（应该能）
 *
 *  对象内存回收
 *      引用计数法
 *      可达性分析算法
 *      常见引用类型: 强引用、软引用、弱引用、虚引用
 *      finalize()方法最终判定对象是否存活
 *      如何判断一个类是无用的类
 *          该类所有的对象实例都已经被回收，也就是 Java 堆中不存在该类的任何实例。
 *          加载该类的 ClassLoader 已经被回收。
 *          该类对应的 java.lang.Class 对象没有在任何地方被引用，无法在任何地方通过反射访问该类的方法。
 *
 *  垃圾收集算法
 *      分代收集理论  新生代（99%对象会消失）且有老年代做担保，使用标记-复制算法；老年代选择“标记-清除”或“标记-整理”，该算法比复制算法慢10倍以上
 *      标记-复制算法（新生代）
 *      标记-清除（老年代）
 *      标记-整理（老年代）
 *如果说收集算法是内存回收的方法论，那么垃圾收集器就是内存回收的具体实现。
 *  垃圾收集器
 *      Serial收集器(-XX:+UseSerialGC  -XX:+UseSerialOldGC) "Stop The World"
 *      Parallel Scavenge收集器(-XX:+UseParallelGC(年轻代),-XX:+UseParallelOldGC(老年代))
 *      ParNew收集器(-XX:+UseParNewGC)  跟Parallel收集器很类似，区别主要在于它可以和CMS收集器配合使用
 *      CMS收集器(-XX:+UseConcMarkSweepGC(old))  亿级流量电商系统如何优化JVM参数设置(ParNew+CMS)
 *
 *      G1收集器(-XX:+UseG1GC)   -XX:G1HeapRegionSize  -XX:G1NewSizePercent  -XX:G1MaxNewSizePercent   -XX:MaxGCPauseMillis  -XX:InitiatingHeapOccupancyPercent
 *  垃圾收集底层算法实现
 *      三色标记
 *      多标-浮动垃圾
 *      漏标-读写屏障
 *      写屏障
 *
 *
 */
public class Math {
    public static final int initData = 666;// 静态变量 常量池
    public static User user = new User(); //只有在new的时候，才会实例化对象（jvm中存<classname,data>）

    public int compute() {  //一个方法对应一块栈帧内存区域
        int a = 1;
        int b = 2;
        int c = (a + b) * 10;
        return c;
    }

    public static void main(String[] args) {
        Math math = new Math(); // 1 类先加载  static  final  int user      2.创建对象 math  compute
        math.compute();// 3.对象计算
    }
}
