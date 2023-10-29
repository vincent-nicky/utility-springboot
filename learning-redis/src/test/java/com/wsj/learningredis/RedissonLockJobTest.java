package com.wsj.learningredis;

import com.wsj.learningredis.job.RedissonLockJob;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class RedissonLockJobTest {

    private ExecutorService executorService = new ThreadPoolExecutor(
            24,
            100,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Autowired
    private RedissonLockJob redissonLockJob;

    @Test
    void test01() {
        redissonLockJob.doCacheRecommendUser();
    }

    @Test
    void test02() {
        // 模拟多个服务器同时执行一个定时任务时

        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // 异步执行
            System.out.println("开始抢锁");
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                redissonLockJob.doCacheRecommendUser();
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

    }

    @Test
    void test03() {
        // 对比：每一个线程轮流抢到锁的情况
        for (int i = 0; i < 10; i++) {
            // 异步执行
            System.out.println("开始抢锁");
            redissonLockJob.doCacheRecommendUser();
        }
    }

}
