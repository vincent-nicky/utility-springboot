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
