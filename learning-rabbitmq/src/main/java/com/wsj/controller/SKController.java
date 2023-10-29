package com.wsj.controller;

import com.google.gson.Gson;
import com.wsj.bizmq.MqMessageProducer;
import com.wsj.model.dto.SKMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
//@RestController
public class SKController {

    @Autowired
    private MqMessageProducer mqMessageProducer;

    public void SKTest(long userId, long goodsId) {

        // 构造消息体，并发送

        SKMessageDTO SKMessageDTO = new SKMessageDTO();
        SKMessageDTO.setUserId(userId);
        SKMessageDTO.setGoodsId(goodsId);
        String flashSaleMessage = new Gson().toJson(SKMessageDTO);
        mqMessageProducer.sendSKMessage(flashSaleMessage);
    }

}
