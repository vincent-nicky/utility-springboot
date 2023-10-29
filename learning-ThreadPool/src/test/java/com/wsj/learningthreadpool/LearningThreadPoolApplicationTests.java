package com.wsj.learningthreadpool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
class LearningThreadPoolApplicationTests {

    // 核心线程
    private static final int SIZE_CORE_POOL = 6;

    // 最大线程数
    private static final int SIZE_MAX_POOL = 12;

    // 线程池维护线程所允许的空闲时间
    private static final long TIME_KEEP_ALIVE = 1L;

    // 缓冲队列的大小
    private static final int SIZE_WORK_QUEUE = 20;

    // 给线程命名时使用
    AtomicInteger c = new AtomicInteger(1);

    // 自定义一个线程池
    private final ThreadPoolExecutor bizPoolExecutor = new ThreadPoolExecutor(
            SIZE_CORE_POOL,
            SIZE_MAX_POOL,
            TIME_KEEP_ALIVE,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(SIZE_WORK_QUEUE),
            //Executors.defaultThreadFactory(),
            r -> new Thread(r, "poolThread" + c.getAndIncrement()), // 给线程起名
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Test
    void mainTest() {
        for (int i = 0; i < 10; i++) {
            test1();
        }
        // 重置计数器
        c = new AtomicInteger(1);
        // 关闭线程池
        bizPoolExecutor.shutdown();
    }

    void test1() {
        // 创建异步任务（无返回）
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // 提交一些任务到线程池
            System.out.println("test1 executed by " + Thread.currentThread().getName());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("执行完毕");
        }, bizPoolExecutor);
        //future.join();
    }

    void test2() {
        // 创建异步任务（无返回）
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // 提交一些任务到线程池
            System.out.println("test2 executed by " + Thread.currentThread().getName());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("执行完毕");
        }, bizPoolExecutor);
        // 如果没有join，任务没有全部执行完就结束了
        //future.join();
    }

    void test3() {

        // 自定义一个线程池
        final ThreadPoolExecutor bizPoolExecutor2 = new ThreadPoolExecutor(
                SIZE_CORE_POOL,
                SIZE_MAX_POOL,
                TIME_KEEP_ALIVE,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(SIZE_WORK_QUEUE),
                //Executors.defaultThreadFactory(),
                r -> new Thread(r, "poolThread" + c.getAndIncrement()), // 给线程起名
                new ThreadPoolExecutor.CallerRunsPolicy());

        // 创建异步任务（无返回）
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            // 提交一些任务到线程池
            System.out.println("Task executed by " + Thread.currentThread().getName());
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }, bizPoolExecutor2);

        //future.join();

        bizPoolExecutor2.shutdown();

    }

    @Test
    void test4() {
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            System.out.println("future1 开始执行");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future1 执行完毕");
        });

        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            System.out.println("future2 开始执行");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future2 执行完毕");
        });

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            System.out.println("future3 开始执行");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("future3 执行完毕");
        });

        // 如果不加这句线程没执行完主线程就结束了，要确保所有的线程执行结束后才运行主程序
        CompletableFuture.allOf(future1, future2, future3).join();
    }

}
