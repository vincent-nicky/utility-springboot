## 一、前置知识

参考文档：[Java线程池 - 知识库](https://charon1030.top/pages/39e24a/#_3-1-%E8%AF%B4%E4%B8%80%E4%B8%8B%E7%BA%BF%E7%A8%8B%E6%B1%A0%E7%9A%84%E6%A0%B8%E5%BF%83%E5%8F%82%E6%95%B0-%E7%BA%BF%E7%A8%8B%E6%B1%A0%E7%9A%84%E6%89%A7%E8%A1%8C%E5%8E%9F%E7%90%86%E7%9F%A5%E9%81%93%E5%98%9B)

![image-20231027184554501](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20231027184554501.png)

![image-20220821003816845](https://cdn.jsdelivr.net/gh/vincent-nicky/image_store/blog/image-20220821003816845.png)

## 二、笔记

### 问题一：这里循环了100次，为什么最终只执行了98次？

```java
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
        new ThreadPoolExecutor.CallerRunsPolicy());

@Test
void void mainTest(){
    for (int i = 0; i < 100; i++) {
        test1();
    }
    // 重置计数器
    c = new AtomicInteger(1);
    // 关闭线程池
    bizPoolExecutor.shutdown();
}
@Test
void test1() {
    // 创建异步任务（无返回）
    CompletableFuture future = CompletableFuture.runAsync(() -> {
        // 提交一些任务到线程池
        System.out.println("Task executed by " + Thread.currentThread().getName());
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }, bizPoolExecutor);
}
```

### 解决

增加核心线程数、最大线程数、缓冲队列的大小

### 问题二：如果没有join，任务没有全部执行完就结束了

```java
@Test
void test2() {
    // 创建异步任务（无返回）
    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        // 提交一些任务到线程池
        System.out.println("Task executed by " + Thread.currentThread().getName());
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
```



### 问题二：循环了多少次就有多少个线程

```java
@Test
void mainTest() {
    for (int i = 0; i < 100; i++) {
        test3();
    }
    // 重置计数器
    c = new AtomicInteger(1);
}

@Test
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
```

### 解决

注意使用的逻辑

### 注意：要确保所有的线程执行结束后才运行主程序

```java
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
    CompletableFuture.allOf(future1,future2,future3).join();
}
```

### 带返回值的多线程

```java
CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() ->
        pictureDataSource.doSearch(searchText, 1, 20)
);

CompletableFuture<Page<UserVO>> userTask = CompletableFuture.supplyAsync(() -> {
    UserQueryRequest userQueryRequest = new UserQueryRequest();
    userQueryRequest.setUserName(searchText);
    return userDataSource.doSearch(searchText, current, pageSize);
});

CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() -> {
    PostQueryRequest postQueryRequest = new PostQueryRequest();
    postQueryRequest.setSearchText(searchText);
    return postDataSource.doSearch(searchText, current, pageSize);
});

CompletableFuture<Page<VideoVo>> videoTask = CompletableFuture.supplyAsync(() ->
        videoDataSource.doSearch(searchText, current, pageSize)
);

CompletableFuture.allOf(pictureTask, userTask, pictureTask).join();
```



## 三、应用

### 1、批量保存数据到mysql

原：（56 秒 10 万条）

```java
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
    userInfoService.saveBatch(userList, 10000);
    stopWatch.stop();
    System.out.println(stopWatch.getTotalTimeMillis());
}
```

使用线程池：（10 秒 10 万条）

```java
private ExecutorService executorService = new ThreadPoolExecutor(
        24,
        100,
        10,
        TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(10000),
        Executors.defaultThreadFactory(),
        new ThreadPoolExecutor.CallerRunsPolicy()
);

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
```

### 2、封装并使用线程池

封装：

```java
package com.wsj.learningmultithread.config;

import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolConfig {

    // 核心线程
    private static final int SIZE_CORE_POOL = 6;

    // 最大线程数
    private static final int SIZE_MAX_POOL = 12;

    // 线程池维护线程所允许的空闲时间
    private static final long TIME_KEEP_ALIVE = 1L;

    // 缓冲队列的大小
    private static final int SIZE_WORK_QUEUE = 20;

    // 自定义一个线程池
    public static ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(
                SIZE_CORE_POOL,
                SIZE_MAX_POOL,
                TIME_KEEP_ALIVE,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(SIZE_WORK_QUEUE),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
```

使用

```java
package com.wsj.learningmultithread.manager;

import com.wsj.learningmultithread.config.ThreadPoolConfig;
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
```

