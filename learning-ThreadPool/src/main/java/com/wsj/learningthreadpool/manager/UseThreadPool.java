package com.wsj.learningthreadpool.manager;

import com.wsj.learningthreadpool.config.ThreadPoolConfig;

import java.util.concurrent.CompletableFuture;

public class UseThreadPool {

    public static void main(String[] args) {

        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println("future1 开始执行");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future1 执行完毕");
        }, ThreadPoolConfig.threadPoolExecutor());

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("future2 开始执行");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future2 执行完毕");
        }, ThreadPoolConfig.threadPoolExecutor());

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            System.out.println("future3 开始执行");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future3 执行完毕");
        }, ThreadPoolConfig.threadPoolExecutor());

        // 如果不加这句线程没执行完主线程就结束了，要确保所有的线程执行结束后才运行主程序
        CompletableFuture.allOf(future1, future2, future3).join();

    }
}
