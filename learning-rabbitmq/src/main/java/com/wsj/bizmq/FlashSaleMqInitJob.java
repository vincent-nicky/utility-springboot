package com.wsj.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 用于创建测试程序用到的交换机和队列（只用在程序启动前执行一次）
 */
// todo 取消注释开启任务
//@Component
public class FlashSaleMqInitJob implements CommandLineRunner {

    @Override
    public void run(String... args) {
        try {
            // 建立连接通道
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("192.168.50.129");
            factory.setUsername("admin");
            factory.setPassword("123456");
            factory.setPort(5672);
            Channel channel = factory.newConnection().createChannel();
            // 创建交换机
            channel.exchangeDeclare(FlashSaleMqConstant.FLASH_SALE_EXCHANGE, "direct");
            // 创建队列
            channel.queueDeclare(FlashSaleMqConstant.FLASH_SALE_QUEUE, true, false, false, null);
            // 绑定
            channel.queueBind(FlashSaleMqConstant.FLASH_SALE_QUEUE, FlashSaleMqConstant.FLASH_SALE_EXCHANGE,  FlashSaleMqConstant.FLASH_SALE_ROUTING_KEY);

            System.out.println("rabbitmq初始化成功");
            //System.exit(0);
        } catch (Exception e) {
            System.out.println("rabbitmq初始化失败");
            //System.exit(1);
        }
    }
}
