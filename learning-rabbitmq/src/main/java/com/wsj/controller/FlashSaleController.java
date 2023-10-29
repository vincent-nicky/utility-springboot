package com.wsj.controller;

import com.google.gson.Gson;
import com.wsj.bizmq.MqMessageProducer;
import com.wsj.model.dto.FlashSaleMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@RestController
public class FlashSaleController {

    @Autowired
    private MqMessageProducer mqMessageProducer;

    public void flashSaleTest(long userId, long goodsId) {

        // 构造消息体，并发送

        FlashSaleMessageDTO flashSaleMessageDTO = new FlashSaleMessageDTO();
        flashSaleMessageDTO.setUserId(userId);
        flashSaleMessageDTO.setGoodsId(goodsId);
        String flashSaleMessage = new Gson().toJson(flashSaleMessageDTO);
        mqMessageProducer.sendFlashSaleMessage(flashSaleMessage);
    }

}
