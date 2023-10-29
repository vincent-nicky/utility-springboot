package com.wsj.bizmq;

import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;

@Component
@Slf4j
public class FlashSaleMessageConsumer {

    // 指定程序监听的消息队列和确认机制
    @SneakyThrows
    @RabbitListener(queues = {FlashSaleMqConstant.FLASH_SALE_QUEUE}, ackMode = "MANUAL")
    public void receiveMessage(String flashSaleMessage, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        if (StringUtils.isBlank(flashSaleMessage)) {
            // 手动消息拒绝
            channel.basicNack(deliveryTag, false, false);
            throw new RuntimeException("消息为空");
        }

        log.info("receiveMessage message：" + flashSaleMessage);

        // 消息确认
        channel.basicAck(deliveryTag, false);
    }

}
