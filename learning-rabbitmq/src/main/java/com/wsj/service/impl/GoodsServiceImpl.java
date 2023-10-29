package com.wsj.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.mapper.GoodsMapper;
import com.wsj.model.Goods;
import com.wsj.service.GoodsService;
import org.springframework.stereotype.Service;

/**
* @author 86178
* @description 针对表【goods】的数据库操作Service实现
* @createDate 2023-10-29 00:41:26
*/
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
    implements GoodsService {

    @Override
    public void doFlashSale(long userId, long goodsId) {
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

        //String redissonLock = "flashSaleLock-userId:" + userId;
        //String flashSaleResult = redissonLockUtil.redissonLock(redissonLock, () -> {
        //    // 模拟一些业务逻辑（只能有一个线程可执行）
        //    Goods goods = goodsService.getById(1);
        //    if(goods.getCount() == 0){
        //        return "来晚了，商品已被抢空！";
        //    } else {
        //        // 这里还应该检查该用户是否已经抢购过
        //        UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        //        updateWrapper.set("count", goods.getCount() - 1);
        //        goodsService.update(updateWrapper);
        //        return "秒杀成功！";
        //    }
        //});
        //System.out.println(flashSaleResult);
    }
}




