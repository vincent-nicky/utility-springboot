package com.wsj.learningredis.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.learningredis.Utils.RedissonLockUtil;
import com.wsj.learningredis.manager.RedisLimiterManager;
import com.wsj.learningredis.mapper.GoodsMapper;
import com.wsj.learningredis.model.Goods;
import com.wsj.learningredis.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author 86178
 * @description 针对表【goods】的数据库操作Service实现
 * @createDate 2023-10-29 00:41:26
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedissonLockUtil redissonLockUtil;

    @Autowired
    private RedisLimiterManager redisLimiterManager;

    @Override
    public boolean doSK(long userId, long goodsId) {
        // 做限流（可选）
        //redisLimiterManager.doRateLimit("limit:" + userId);

        //return doSKDefault(userId,goodsId);
        //return doSKByLock(userId,goodsId);
        return doSKByLockUtil(userId,goodsId);

    }

    private boolean doSKDefault(long userId, long goodsId) {
        Goods goods = this.getById(goodsId);
        if (goods.getCount() == 0) {
            return false;
        } else {
            // 这里还应该检查该用户是否已经抢购过
            UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
            updateWrapper.set("count", goods.getCount() - 1);
            return this.update(updateWrapper);
        }
    }

    private boolean doSKByLock(long userId, long goodsId) {
        String lockKey = "seckill:RedissonLock:orderId:" + userId + "-sk-" +goodsId;
        RLock rLock = redissonClient.getLock(lockKey);
        try {
            // 尝试加锁，每个线程拿不到锁的时候最多等待3秒，每个线程的锁最长可持续20秒
            boolean flag = rLock.tryLock(3, 20, TimeUnit.SECONDS);
            if (flag) {
                return doSKDefault(userId,goodsId);
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new RuntimeException("未知错误");
        } finally {
            if (rLock.isLocked()) {
                if (rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }
        }
    }

    private boolean doSKByLockUtil(long userId, long goodsId) {
        String lockKey = "seckill:RedissonLock:orderId:" + userId + "-sk-" +goodsId;
        return redissonLockUtil.redissonLock(lockKey,5000,10000, () -> doSKDefault(userId,goodsId));
    }
}




