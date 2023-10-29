package com.wsj.learningthreadpool;

import com.wsj.learningthreadpool.entity.UserInfo;
import com.wsj.learningthreadpool.service.UserInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * TODO 导入用户测试
 */
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserInfoService userInfoService;

    // CPU 密集型：分配的核心线程数 = cpu -1
    // IO 密集型: 分配的核心线程数 可以大于CPU核心数
    private ExecutorService executorService = new ThreadPoolExecutor(
            24,
            100,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 批量插入用户
     */
    @Test
    void doInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 100000;
        List<UserInfo> userList = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            UserInfo user = new UserInfo();
            user.setUsername("张三" + i);
            userList.add(user);
        }
        // 56 秒 10 万条
        userInfoService.saveBatch(userList, 10000);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 分十组
        int batchSize = 5000;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<UserInfo> userList = new ArrayList<>();
            do {
                j++;
                UserInfo user = new UserInfo();
                user.setUsername("张三" + i);
                userList.add(user);
            } while (j % batchSize != 0);
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " + Thread.currentThread().getName());
                userInfoService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
