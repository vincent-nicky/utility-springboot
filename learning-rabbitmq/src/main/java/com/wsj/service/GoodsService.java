package com.wsj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.model.Goods;

/**
* @author 86178
* @description 针对表【goods】的数据库操作Service
* @createDate 2023-10-29 00:41:26
*/
public interface GoodsService extends IService<Goods> {

    void doFlashSale(long userId, long goodsId);

}
