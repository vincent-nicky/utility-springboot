package com.wsj.Utils;

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

    /**
     * @param lockName 锁的唯一识别
     * @param waitTime 线程的等待时间
     * @param keepTime 线程持有锁的最大时间
     * @param supplier 执行
     * @return 执行结果
     */
    public <T> T redissonLock(String lockName,long waitTime,long keepTime, Supplier<T> supplier) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            boolean isLocked = rLock.tryLock(waitTime, keepTime, TimeUnit.MILLISECONDS);
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
