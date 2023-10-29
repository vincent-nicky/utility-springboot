package com.wsj.learningredis;

import com.wsj.learningredis.Utils.RedissonLockUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@SpringBootTest
public class RedissonLockUtilTest {

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
    private RedissonLockUtil redissonLockUtil;

    @Test
    void test01() {
        String redissonLock = "userId-1";
        System.out.println( redissonLockUtil.redissonLock(redissonLock, 100,200,() -> "成功执行"));
    }

    @Test
    void test02() {
        String redissonLock = "userId-1";
        Integer sum =  redissonLockUtil.redissonLock(redissonLock, 100,200,() -> {
            // 模拟一些业务逻辑（只能有一个线程可执行）
            int sumR = 0;
            for (int i = 0; i < 10; i++) {
                sumR++;
            }
            return sumR;
        });
        if(sum != null){
            System.out.println(sum);
        }
    }

    @Test
    void testMain() {
        // 模拟多个服务器同时执行一个任务
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            // 异步执行
            System.out.println("开始抢锁");
            futureList.add(CompletableFuture.runAsync(this::test02, executorService));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();

    }

}
