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
