package com.wsj.bizmq;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.wsj.model.dto.SKMessageDTO;
import com.wsj.service.GoodsService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SKMessageConsumer {

    @Autowired
    private GoodsService goodsService;

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {SKMqConstant.FLASH_SALE_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String flashSaleMessage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        if (StringUtils.isBlank(flashSaleMessage)) {
            // 手动消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new RuntimeException("消息为空");
        }

        log.info("receiveMessage message：" + flashSaleMessage);

        SKMessageDTO SKMessageDTO = new Gson().fromJson(flashSaleMessage, SKMessageDTO.class);
        boolean isSuccess = goodsService.doSK(SKMessageDTO.getUserId(), SKMessageDTO.getGoodsId());

        if(isSuccess){
            System.out.println("秒杀成功！");
        }else{
            System.out.println("来晚了，商品已被抢空！");
        }

        // 消息确认
        channel.basicAck(deliveryTag, false);
    }

}
