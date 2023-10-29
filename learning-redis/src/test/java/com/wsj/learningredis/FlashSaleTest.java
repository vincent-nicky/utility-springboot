package com.wsj.learningredis;

import com.wsj.learningredis.controller.FlashSaleController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class FlashSaleTest {

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
    private FlashSaleController flashSaleController;

    // 还需结合消息队列
    @Test
    void testMain() {
        // 模拟多个服务器同时执行一个任务
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // 异步执行
            System.out.println("开始抢锁");
            futureList.add(CompletableFuture.runAsync(() ->{
                flashSaleController.flashSaleTest(1);
            }, executorService));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

    }
}
