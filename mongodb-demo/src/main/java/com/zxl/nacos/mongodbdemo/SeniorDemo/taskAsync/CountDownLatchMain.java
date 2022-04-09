package com.zxl.nacos.mongodbdemo.SeniorDemo.taskAsync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class CountDownLatchMain {

    public static void main(String[] args) throws InterruptedException {

        ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16, 5, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(20), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());

        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38);

        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        List<Integer> evenNumbers = new ArrayList<>();
        list.stream().forEach(item -> {

            executor.submit(() -> {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (item % 2 == 0) {
                    System.out.println(Thread.currentThread().getName()+"====判断偶数的线程。");
                    evenNumbers.add(item);
                }
                countDownLatch.countDown();
            });

        });

        //主线程阻塞状态
        System.out.println(Thread.currentThread().getName()+"====主线程在等待中。");
        long start = System.currentTimeMillis();
        countDownLatch.await();
        long end = System.currentTimeMillis();
        System.out.println(Thread.currentThread().getName()+"====主线程在等待完毕,耗时："+(end-start)/1000);


        evenNumbers.stream().forEach( even -> {
            executor.submit(() -> {
                System.out.println(Thread.currentThread().getName()+"======偶数："+even);
            });
        });


        executor.shutdown();
        System.out.println(Thread.currentThread().getName()+"关闭线程池");

    }

}
