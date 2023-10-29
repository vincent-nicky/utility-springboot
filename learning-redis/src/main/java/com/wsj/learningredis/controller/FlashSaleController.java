package com.wsj.learningredis.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.wsj.learningredis.Utils.RedissonLockUtil;
import com.wsj.learningredis.model.Goods;
import com.wsj.learningredis.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
//@RestController
public class FlashSaleController {

    @Autowired
    private RedissonLockUtil redissonLockUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private GoodsService goodsService;

    public void flashSaleTest(int userId) {

        //Goods goods = goodsService.getById(1);
        //if (goods.getCount() == 0) {
        //    System.out.println("来晚了，商品已被抢空！");
        //} else {
        //    // 这里还应该检查该用户是否已经抢购过
        //    UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        //    updateWrapper.set("count", goods.getCount() - 1);
        //    goodsService.update(updateWrapper);
        //    System.out.println("秒杀成功！");
        //}

        String redissonLock = "flashSaleLock-userId:" + userId;
        String flashSaleResult = redissonLockUtil.redissonLock(redissonLock, () -> {
            // 模拟一些业务逻辑（只能有一个线程可执行）
            Goods goods = goodsService.getById(1);
            if(goods.getCount() == 0){
                return "来晚了，商品已被抢空！";
            } else {
                // 这里还应该检查该用户是否已经抢购过
                UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("count", goods.getCount() - 1);
                goodsService.update(updateWrapper);
                return "秒杀成功！";
            }
        });
        System.out.println(flashSaleResult);
    }

}
