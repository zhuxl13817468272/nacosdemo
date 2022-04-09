package com.zxl.nacos.mongodbdemo.SeniorDemo.taskAsync;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureMain1 {

    /**
     *   CompletableFuture并行异步处理类使用示例: https://zhangxueliang.blog.csdn.net/article/details/103408230
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        //thenApply接受上一阶段的输出作为本阶段的输入，本结算的输出作为输出结果
        //thenAccept 接受上一阶段的输出作为本阶段的输入，没有结果输出
        //thenRun根本不关心前一阶段的输出，因为不需要参数

        //future有局限性，不能实现  多个future结果之间的依懒性

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> 1)
                .thenApply(i -> i + 1)
                .thenApply(i -> {
                    return i * i;
                })
//                .exceptionally()  //catch
                .whenComplete((r, e) -> System.out.println(r));//finally


        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> "hello")
                .thenApply(s -> s + " world")
                .thenApply(String::toUpperCase);
        try {
            while (future1.isDone()) {
                System.out.println("CompletableFuture<Integer> future1  = " + future1.get());
                break;
            }
            System.out.println(completableFuture.get());
        } catch (Exception e) {
        }


    }


}
