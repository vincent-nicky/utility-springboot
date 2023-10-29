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
