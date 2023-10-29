package com.wsj.bizmq;

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
    public void sendSKMessage(String flashSaleMessage) {
        rabbitTemplate.convertAndSend(SKMqConstant.FLASH_SALE_EXCHANGE, SKMqConstant.FLASH_SALE_ROUTING_KEY, flashSaleMessage);
    }

}
