package com.zxl.nacos.mongodbdemo.SeniorDemo.taskAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *  多任务并行
 *        多任务不相关联并行   CompletableFuture.allOf(runAsync1).join()   测试发现，最多只开五个异步线程并行执行任务，示例见：TaskAsyncAllOfJoinMain2
 *        多任务相关联并行     thenCombine 此阶段与其他阶段一起完成，进而触发下一阶段，示例见：TaskAsyncCombineMain3
 */
public class TaskAsyncAllOfJoinMain2 {

    public static void main(String[] args) throws InterruptedException {

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38);

        List<CompletableFuture<Void>> evenNumbers = new ArrayList<>();
        for (int i = 0; i <list.size() ; i++) {
            evenNumbers.add(getRunAsync());
        }

        //主线程阻塞状态
        System.out.println(Thread.currentThread().getName()+"====主线程在等待中。");
        long start = System.currentTimeMillis();
        evenNumbers.forEach((runAsync1)->{
            CompletableFuture.allOf(runAsync1).join();
        });
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName()+"====主线程在等待完毕,耗时："+(end-start)/1000);

        System.out.println(Thread.currentThread().getName()+"关闭线程池");

    }

    public static CompletableFuture<Void> getRunAsync(){
        return  CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000L);
                System.out.println(Thread.currentThread().getName()+"====异步线程名。");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
