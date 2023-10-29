//package com.wsj.learningredis.controller;
//
//import com.wsj.learningredis.service.GoodsService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
////@RestController
//@Slf4j
//public class SKController {
//
//    @Autowired
//    private GoodsService goodsService;
//
//    public void flashSaleTest(long userId, long goodsId) {
//
//        boolean isSuccess = goodsService.doSK(userId, goodsId);
//
//        if (isSuccess) {
//            log.info("秒杀成功！");
//        } else {
//            log.info("来晚了，商品已被抢空！");
//        }
//    }
//
//}
