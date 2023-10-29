package com.wsj;

import com.wsj.controller.SKController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class SKMqTest {

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
    private SKController SKController;

    // 结合消息队列
    @Test
    void test01() {
        // 模拟多个服务器同时执行一个任务
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            // 异步执行
            System.out.println("开始秒杀");
            futureList.add(CompletableFuture.runAsync(() -> {
                SKController.SKTest(1, 1);
            }, executorService));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    }
}
