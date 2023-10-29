package com.wsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.Utils.RedissonLockUtil;
import com.wsj.mapper.GoodsMapper;
import com.wsj.model.Goods;
import com.wsj.service.GoodsService;
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
    private RedissonLockUtil redissonLockUtil;

    @Override
    public boolean doSK(long userId, long goodsId) {

        // 不结合分布式锁
        //Goods goods = this.getById(1);
        //if (goods.getCount() <= 0) {
        //    return false;
        //} else {
        //    UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        //    updateWrapper.set("count", goods.getCount() - 1);
        //    return this.update(updateWrapper);
        //}

        // 结合分布式锁
        String lockKey = "seckill:RedissonLock:orderId:" + userId + "-sk-" + goodsId;
        return redissonLockUtil.redissonLock(lockKey, 5000, 10000, () -> {
            Goods goods = this.getById(1);
            if (goods.getCount() <= 0) {
                return false;
            } else {
                UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("count", goods.getCount() - 1);
                return this.update(updateWrapper);
            }
        });
    }
}