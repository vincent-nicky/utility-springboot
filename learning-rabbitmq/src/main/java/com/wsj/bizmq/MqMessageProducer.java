package com.wsj.bizmq;

import com.wsj.model.dto.FlashSaleMessageDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MqMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     */
    public void sendFlashSaleMessage(String flashSaleMessage) {
        rabbitTemplate.convertAndSend(FlashSaleMqConstant.FLASH_SALE_EXCHANGE, FlashSaleMqConstant.FLASH_SALE_ROUTING_KEY, flashSaleMessage);
    }

}
