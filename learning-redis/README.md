## 前置知识

基于`Redis`的分布式缓存，基于 `Redisson` 的分布式锁、分布式限流。

参考：[最强分布式锁工具：Redisson](https://mp.weixin.qq.com/s?__biz=MzU0OTE4MzYzMw==&mid=2247545794&idx=2&sn=88a3b1c73372006b49a43a6c133a10c3&chksm=fbb1ba3cccc6332ae1f5e609ab5e37c32fe972b7ba3a18b1a92735c5f3e7c9300e2318ca2280&scene=27)

Github：[https://github.com/redisson/redisson](https://github.com/redisson/redisson)

examples：[https://github.com/redisson/redisson-examples](https://github.com/redisson/redisson-examples)

## 一、分布式缓存

[单点登录（Single Sign On），简称为 SSO - 百度百科](https://baike.baidu.com/item/%E5%8D%95%E7%82%B9%E7%99%BB%E5%BD%95/4940767?fr=ge_ala)

1、引入依赖

```xml
<!-- redis -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.session</groupId>
    <artifactId>spring-session-data-redis</artifactId>
</dependency>
```

2、配置文件

```yml
spring:
  redis:
    database: 2
    host: localhost
    port: 6379
    timeout: 5000
  session:
    # 开启分布式 session（须先配置 Redis）
    store-type: redis
    # 30 天过期
    timeout: 2592000
```

3、使用

```java
// 写入
request.getSession().setAttribute(USER_LOGIN_STATE, user);

// 获取
Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
```



## 二、分布式锁

### 1、缓存预热：保证定时任务只需一个服务器执行

原任务：

```java
package com.wsj.learningredis.job;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存预热任务
 *
 */
@Component
@Slf4j
public class PreCacheJob {

    //@Resource
    //private UserService userService;

    //@Autowired
    //private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    // 重点用户
    //private List<Long> mainUserList = Arrays.asList(1L);

    // 每天执行，预热推荐用户
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        // 获取锁的信息
        RLock lock = redissonClient.getLock("project:precachejob:lock");
        try {
            // 根据锁的信息获取锁
            boolean isLocked = lock.tryLock(0, 60, TimeUnit.SECONDS);
            /*
                tryLock 是尝试获取锁的方法。
                0 是等待时间，表示如果锁不可用，不要等待，立即返回结果。
                -1 表示锁的持续时间，即锁不会自动释放，必须手动释放。
                TimeUnit.SECONDS 表示等待时间的单位，这里是秒。
             */
            // 如果成功获取锁
            if (isLocked) {
                System.out.println("getLock: " + Thread.currentThread().getId() + " 抢到了");
                Thread.sleep(3000);

                //// 执行业务逻辑
                //// 每天为每一位用户推荐20位佳友
                //List<Long> mainUserList = userService.selectAllUserId();
                //for (Long userId : mainUserList) {
                //    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                //    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                //    String redisKey = String.format("project:user:recommend:to:%s", userId);
                //    // 写缓存
                //    redisTemplate.opsForValue().set(redisKey, userPage, 1, TimeUnit.DAYS);
                //}
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                //System.out.println("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }
}
```

---

（删除一些注释）

```java
@Component
@Slf4j
public class PreCacheJob {
    @Autowired
    private RedissonClient redissonClient;
    // 定时执行
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        // 获取锁的信息
        RLock lock = redissonClient.getLock("project:precachejob:lock");
        try {
            // 根据锁的信息获取锁
            boolean isLocked = lock.tryLock(0, 60, TimeUnit.SECONDS);
            // 如果成功获取锁
            if (isLocked) {
                // 执行业务逻辑
                System.out.println("getLock: " + Thread.currentThread().getId() + " 抢到了");
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

---

测试

```java
@Test
void test02() {
    // 模拟多个服务器同时执行一个定时任务时
    List<CompletableFuture<Void>> futureList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
        // 异步执行
        System.out.println("开始抢锁");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            preCacheJob.doCacheRecommendUser();
        }, executorService);
        futureList.add(future);
    }
    CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
}
```

---

对比：修改了 `lock.tryLock` 的参数

每一个线程拿到锁的持续时间：2秒

每一个线程等待抢锁的时间：3秒

最终每一个线程都拿到锁。

```java
@Component
@Slf4j
public class PreCacheJob {
    @Autowired
    private RedissonClient redissonClient;
    // 定时执行
    @Scheduled(cron = "0 31 0 * * *")
    public void doCacheRecommendUser() {
        // 获取锁的信息
        RLock lock = redissonClient.getLock("project:precachejob:lock");
        try {
            // 根据锁的信息获取锁
            boolean isLocked = lock.tryLock(3, 2, TimeUnit.SECONDS);
            // 如果成功获取锁
            if (isLocked) {
                // 执行业务逻辑
                System.out.println("getLock: " + Thread.currentThread().getId() + " 抢到了");
                Thread.sleep(3000);
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser error", e);
        } finally {
            // 释放自己的锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
```

### 2、订单支付

最好加上可持久化的消息队列。

创建 `redissonLockUtil`

```java
public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
    RLock rLock = redissonClient.getLock(lockName);
    try {
        if (rLock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
            return supplier.get();
        }
        throw new BusinessException(errorCode.getCode(), errorMessage);
    } catch (Exception e) {
        throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
    } finally {
        if (rLock.isHeldByCurrentThread()) {
            log.error("unLock: " + Thread.currentThread().getId());
            rLock.unlock();
        }
    }
}
```

---

防止多个线程创建订单：

```java
// 通过 .intern() 方法可以将字符串池化，以节省内存。
String redissonLock = ("getOrder_" + loginUser.getUserAccount()).intern();
ProductOrderVo getProductOrderVo = redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
    // 订单存在就返回不再新创建
    return productOrderService.getProductOrder(productId, loginUser, payType);
});

// 下面是创建订单的逻辑
```



```java
String redissonLock = ("createOrder_" + loginUser.getUserAccount()).intern();
return redissonLockUtil.redissonDistributedLocks(redissonLock, () -> {
    // 检查是否有购买该订单的记录
    checkBuyRechargeActivity(loginUser.getId(), productId);
    // 保存订单,返回vo信息
    return productOrderService.saveProductOrderFree(productId, loginUser);
});
```

如果这里不使用分布式锁，有可能有多个线程同时查到 没有该订单的购买记录 就同时执行保存订单的逻辑，导致同一个订单有多份入库记录。

### 3、设计一个通用util

这里如果把 `rLock.tryLock` 放到条件语句中，会让获取不到锁的线程报异常

```java
package com.wsj.learningredis.Utils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
@Component
@Slf4j
public class RedissonLockUtil {
    @Autowired
    private RedissonClient redissonClient;
    public <T> T redissonLock(String lockName, Supplier<T> supplier) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = rLock.tryLock(0, -1, TimeUnit.MILLISECONDS);
            if (isLocked) {
                return supplier.get();
            }
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException("未知异常");
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }
}

```

测试

```java
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
        System.out.println( redissonLockUtil.redissonLock(redissonLock, () -> "成功执行"));
    }
    @Test
    void test02() {
        String redissonLock = "userId-1";
        Integer sum =  redissonLockUtil.redissonLock(redissonLock, () -> {
            // 模拟一些业务逻辑（只能有一个线程可执行）
            int sumR = 0;
            for (int i = 0; i < 10; i++) { sumR++; }
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
```



## 三、分布式限流

创建文件：

```java
package com.wsj.learningredis.manager;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
/**
 * 专门提供 RedisLimiter 限流基础服务的（提供了通用的能力）
 */
@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;
    /**
     * 限流操作
     * <p>
     * 使用示例：redisLimiterManager.doRateLimit("userRequestBy：" + loginUser.getId());
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new RuntimeException("Too many request！");
        }
    }
    /*
        解析：
        定义了一个名为doRateLimit的方法，它接受一个名为key的参数。这个键可能用于标识特定资源或用户的速率限制。
        在方法内部，使用redissonClient创建了一个Redisson RRateLimiter实例，并与指定的key相关联。
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS)用于配置速率限制。这一行设置了一个速率限制器，使其允许每秒最多2次请求。
            RateType.OVERALL 指定速率限制适用于所有请求。
            2 表示指定时间间隔（在本例中为1秒）内允许的最大请求数。
            1 表示时间间隔，这里是1秒。
            RateIntervalUnit.SECONDS 指定时间单位（在本例中为秒）。
        配置速率限制器后，代码尝试通过调用rateLimiter.tryAcquire(1)来获取一个令牌。这是尝试获取一个用于执行操作的令牌。如果成功获取令牌，这意味着请求在速率限制内，方法将继续执行。
        如果没有可用令牌，这意味着速率限制已超过，将抛出一个带有消息 "Too many requests!" 的RuntimeException异常。
     */
}
```

开始测试

将`redisLimiterManager.doRateLimit`放到要执行的业务中

```java
package com.wsj.learningredis;
import com.wsj.learningredis.manager.RedisLimiterManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
@SpringBootTest
class RedisLimiterManagerTest {
    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Test
    void doRateLimit() throws InterruptedException {
        String userId = "1";
        for (int i = 0; i < 2; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for (int i = 0; i < 3; i++) {
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }
}
```



