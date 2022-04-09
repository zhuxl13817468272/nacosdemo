package com.zxl.nacos.mongodbdemo.SeniorDemo.taskAsync;

import java.util.concurrent.CompletableFuture;

/**
 * 多任务并行
 *      多任务不相关联并行   CompletableFuture.allOf(runAsync1).join()   测试发现，最多只开五个异步线程并行执行任务，示例见：TaskAsyncAllOfJoinMain2
 *      多任务相关联并行     thenCombine 此阶段与其他阶段一起完成，进而触发下一阶段，示例见：TaskAsyncCombineMain3
 *
 *  3 个任务： 任务 1 负责洗水壶、烧开水，
 *            任务 2 负责洗茶壶、洗茶杯和拿茶叶，
 *            任务 3 负责泡茶。其中任务 3 要等待任务 1 和任务 2 都完成后才能开始。而任务3的线程为任务 1 或任务 2 中耗时最大的那个线程执行
 *
 *   商品详情页面 这种需要从多个系统中查数据显示的，就很适合用CompletableFuture来多线程异步调用，调用完进行异步之间的依赖
 */
public class TaskAsyncCombineMain3 {

    public static void main(String[] args) throws InterruptedException {

        CompletableFuture<Void> runAsync = CompletableFuture.runAsync(() -> {
            System.out.println("T1:洗水壶。。。" + Thread.currentThread().getName());
            sleep(1000);

            System.out.println("T1:烧开水。。。" + Thread.currentThread().getName());
//            sleep(1000);
            sleep(15000);
        });

        CompletableFuture<String> supplyAsync = CompletableFuture.supplyAsync(() -> {
            System.out.println("T2:洗茶壶。。。" + Thread.currentThread().getName());
            sleep(1000);

            System.out.println("T2:洗茶杯。。。" + Thread.currentThread().getName());
            sleep(2000);

            System.out.println("T2:拿茶叶。。。" + Thread.currentThread().getName());
            sleep(1000);
            return "西湖龙井";
        });

        // 其中任务 3 要等待任务 1 和任务 2 都完成后才能开始，而任务3的线程为任务 1 或任务 2 中耗时最大的那个线程执行
        CompletableFuture<String> thenCombine = runAsync.thenCombine(supplyAsync, (__, tf) -> {
            System.out.println("T3:拿到茶叶：" + tf + Thread.currentThread().getName());
            System.out.println("T3:泡茶。。。");
            return "上茶：" + tf;
        });

        // 主线程
        System.out.println(thenCombine.join()+ Thread.currentThread().getName());


        CompletableFuture<Integer> exceptionally = CompletableFuture.supplyAsync(() -> 7 / 0)
                .thenApply(r -> r * 10)
                .exceptionally(e -> 0);
        System.out.println(exceptionally.join());

    }

    static void sleep(Integer t){
        try{
            Thread.currentThread().sleep(t);
        }catch (Exception e){

        }
    }

}
