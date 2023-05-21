package com.example.running.util;

import cn.hutool.core.util.StrUtil;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author hongyuan
 * @since 2022/2/9 11:41
 * 特定订单自动核销实现类
 */
@Service("MqHandleBaseService")
public abstract class RabbitMqHandleBaseService {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMqHandleBaseService.class);

    @Resource
    RabbitTemplate zmRabbitTemplate;

    //@Resource
    //MessageProperties zmMessageProperties;

    @Value("${spring.rabbitmq.config.queue}")
    private final static String queue = "";

    @Value("${spring.rabbitmq.config.exchange}")
    private final static String exchange = "";

    @Value("${spring.rabbitmq.config.routingKey}")
    private final static String routingKey = "";

    /**
     * 开启MQ 消费
     */
    @Value("${spring.rabbitmq.consumer.enable}")
    Boolean consumerEnable;

    @Value("${spring.rabbitmq.messageExpiration}")
    String messageExpiration;

    private long sleepMillis;

    public abstract boolean handleMessage(String message);

    public void sendOilOrders(String exchange, String routingKey, String msg) {
        //Message message = new Message(msg.getBytes(), messageProperties);
        Message message = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_TEXT_PLAIN)
//                .setCorrelationId(uuid)
                .setExpiration(messageExpiration)
                .build();
        //logger.info("RabbitMq:存储消息 message:{} ", msg);
        //zmRabbitTemplate.convertAndSend(exchange, routingKey, msg);
        zmRabbitTemplate.convertAndSend(exchange, routingKey, message);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = queue, durable = "true", autoDelete = "false"),
            exchange = @Exchange(name = exchange, type = "topic", ignoreDeclarationExceptions = "true"),
            key = routingKey))
    @RabbitHandler
    public void onMessage(Message message, Channel channel) {
        if (!consumerEnable)
            return;
        long deliveryTag = 0;
        String messageBody = "";
        try {
            //message 转换
            messageBody = StrUtil.str(message.getBody(), StandardCharsets.UTF_8);
            //获取deliveryTag
            deliveryTag = message.getMessageProperties().getDeliveryTag();
            //由子类处理message 并ack
            boolean ackResult = handleMessage(messageBody);
            if(ackResult){
                channel.basicAck(deliveryTag, false);
                sleepMillis = 500;
            } else {
                logger.error("RabbitMq:onMessage 消费失败 message:{} class:{}", messageBody, getClass().getName());
                resentMessage(channel, messageBody, deliveryTag);
            }
        } catch (Exception e) {
            logger.error("RabbitMq:onMessage 消费异常 message:{} error:{}", messageBody, e.toString());
            resentMessage(channel, messageBody, deliveryTag);
        }
    }

    /**
     * @author hongyuan
     * @since 2022/3/15 18:21
     * 消费失败回馈并重发消息
     */
    private void resentMessage(Channel channel, String messageBody, long deliveryTag) {
        try {
            //channel.basicNack(deliveryTag, false, true);
            channel.basicAck(deliveryTag, false);
            Thread.sleep(sleepMillis);
            sleepMillis += 500;
            RabbitMqHandleBaseService.this.sendOilOrders(SimpleQueue.exchangeSyncOrderName, SimpleQueue.exchangeSyncOrderRoutingKey, messageBody);
            logger.error("RabbitMq:重发消息Ack成功 message:{}", messageBody);
        } catch (Exception e) {
            logger.error("RabbitMq:消息重发异常 message:{} error:{}", messageBody, e.toString());
        }
    }
}