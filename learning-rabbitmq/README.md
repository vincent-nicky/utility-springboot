## 一、前置知识

手动消息确认、消息持久化、交换机队列定义、消息生产消费的实践

官网：[https://www.rabbitmq.com/getstarted.html](https://www.rabbitmq.com/getstarted.html)



### 模型



### 持久化



### 死信队列



## 二、秒杀（消息队列+分布式锁）

查看全部代码，可拷贝项目

### 1、bizmq

SKMqConstant

```java
package com.wsj.bizmq;
public interface SKMqConstant {
    String FLASH_SALE_EXCHANGE = "flashSale_exchange";
    String FLASH_SALE_QUEUE = "flashSale_queue";
    String FLASH_SALE_ROUTING_KEY = "flashSale_routingKey";
}
```

SKMqInitJob

```java
package com.wsj.bizmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
/**
 * 用于创建测试程序用到的交换机和队列
 */
// todo 取消注释开启任务（只在程序启动前执行一次）
//@Component
public class SKMqInitJob implements CommandLineRunner {
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
            channel.exchangeDeclare(SKMqConstant.FLASH_SALE_EXCHANGE, "direct");
            // 创建队列
            channel.queueDeclare(SKMqConstant.FLASH_SALE_QUEUE, true, false, false, null);
            // 绑定
            channel.queueBind(SKMqConstant.FLASH_SALE_QUEUE, SKMqConstant.FLASH_SALE_EXCHANGE,  SKMqConstant.FLASH_SALE_ROUTING_KEY);

            System.out.println("rabbitmq初始化成功");
            //System.exit(0);
        } catch (Exception e) {
            System.out.println("rabbitmq初始化失败");
            //System.exit(1);
        }
    }
}
```

MqMessageProducer

```java
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
```

SKMessageConsumer

```java
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
```

### 2、controller

```java
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
```

### 3、service

```java
package com.wsj.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wsj.model.Goods;
/**
* @author 86178
* @description 针对表【goods】的数据库操作Service
* @createDate 2023-10-29 00:41:26
*/
public interface GoodsService extends IService<Goods> {
    boolean doSK(long userId, long goodsId);
}
```

```java
package com.wsj.service.impl;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wsj.Utils.RedissonLockUtil;
import com.wsj.mapper.GoodsMapper;
import com.wsj.model.Goods;
import com.wsj.service.GoodsService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
/**
 * @author 86178
 * @description 针对表【goods】的数据库操作Service实现
 * @createDate 2023-10-29 00:41:26
 */
@Service
@Slf4j
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods>
        implements GoodsService {
    @Autowired
    private RedissonLockUtil redissonLockUtil;
    @Override
    public boolean doSK(long userId, long goodsId) {

        // 不结合分布式锁
        //Goods goods = this.getById(1);
        //if (goods.getCount() <= 0) {
        //    return false;
        //} else {
        //    UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
        //    updateWrapper.set("count", goods.getCount() - 1);
        //    return this.update(updateWrapper);
        //}

        // 结合分布式锁
        String lockKey = "seckill:RedissonLock:orderId:" + userId + "-sk-" + goodsId;
        return redissonLockUtil.redissonLock(lockKey, 5000, 10000, () -> {
            Goods goods = this.getById(1);
            if (goods.getCount() <= 0) {
                return false;
            } else {
                UpdateWrapper<Goods> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("count", goods.getCount() - 1);
                return this.update(updateWrapper);
            }
        });
    }
}
```

### 4、测试

```java
package com.wsj;
import com.wsj.controller.SKController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
@SpringBootTest
public class SKMqTest {
    private ExecutorService executorService = new ThreadPoolExecutor(
            24,
            100,
            10,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(10000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    @Autowired
    private SKController SKController;
    // 结合消息队列
    @Test
    void test01() {
        // 模拟多个服务器同时执行一个任务
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            // 异步执行
            System.out.println("开始秒杀");
            futureList.add(CompletableFuture.runAsync(() -> {
                SKController.SKTest(1, 1);
            }, executorService));
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
    }
}
```

## 三、一些报错

```
org.springframework.amqp.rabbit.support.ListenerExecutionFailedException: Failed to convert message
	at org.springframework.amqp.rabbit.listener.adapter.MessagingMessageListenerAdapter.onMessage(MessagingMessageListenerAdapter.java:157) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.doInvokeListener(AbstractMessageListenerContainer.java:1722) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.actualInvokeListener(AbstractMessageListenerContainer.java:1641) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.invokeListener(AbstractMessageListenerContainer.java:1629) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.doExecuteListener(AbstractMessageListenerContainer.java:1620) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.AbstractMessageListenerContainer.executeListener(AbstractMessageListenerContainer.java:1564) ~[spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer.doReceiveAndExecute(SimpleMessageListenerContainer.java:994) [spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer.receiveAndExecute(SimpleMessageListenerContainer.java:941) [spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer.access$1600(SimpleMessageListenerContainer.java:86) [spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.mainLoop(SimpleMessageListenerContainer.java:1323) [spring-rabbit-2.4.17.jar:2.4.17]
	at org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer$AsyncMessageProcessingConsumer.run(SimpleMessageListenerContainer.java:1225) [spring-rabbit-2.4.17.jar:2.4.17]
	at java.lang.Thread.run(Thread.java:750) [na:1.8.0_392]
	
Caused by: java.lang.SecurityException: Attempt to deserialize unauthorized class com.wsj.model.dto.SKMessageDTO; add allowed class name patterns to the message converter or, if you trust the message orginiator, set environment variable 'SPRING_AMQP_DESERIALIZATION_TRUST_ALL' or system property 'spring.amqp.deserialization.trust.all' to true
```

