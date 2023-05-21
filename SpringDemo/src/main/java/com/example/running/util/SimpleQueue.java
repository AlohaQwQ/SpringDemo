package com.example.running.util;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 简单模式：直接发送到queue
 *
 * @author chenmeng
 * @date 2022/04/29 17:03
 **/
@Configuration
public class SimpleQueue {

    // 此queue用于测试
    public static final String TEST_QUEUE = "zk.queue.test-queue";
    public static final String ORDER_SEND_BONUS = "zk.queue.order.send-bonus";
    public static final String BONUS_SEND_STATUS = "zk.queue.bonus.send-status";//积分状态队列
    public static final String BONUS_DEAD_SEND_STATUS = "zk.queue.bonus.dead.send-status";//积分状态死信队列

    //自动核销队列
    public static final String exchangeSyncOrderName = "zk.exchange.syncorder";
    public static final String queueSyncOrderName = "zk.queue.syncorder";
    public static final String exchangeSyncOrderRoutingKey = "syncorder.#";

    //积分队列
    public static final String exchangeBonusName = "zk.exchange.bonus.status";

    //积分兑换扣减库存队列
    public static final String exchangeInventoryName = "zk.exchange.inventory";
    public static final String queueInventoryName = "zk.queue.inventory";
    public static final String exchangeInventoryRoutingKey = "inventory.#";

    //核销第三方订单队列
    public static final String exchangeThirdWriteOffName = "zk.exchange.thirdwriteoff";
    public static final String queueThirdWriteOffOrderName = "zk.queue.thirdwriteoff";
    public static final String exchangeThirdWriteOffOrderRoutingKey = "thirdwriteoff.#";

    //核销扣减库存队列
    public static final String exchangeFinishInventoryName = "zk.exchange.finish-inventory";
    public static final String queueFinishInventoryName = "zk.queue.finish-inventory";
    public static final String exchangeFinishInventoryRoutingKey = "finishinventory.#";

    @Bean(BONUS_SEND_STATUS)
    public Queue queue1() {
        return new Queue(BONUS_SEND_STATUS, true);
    }

    @Bean(ORDER_SEND_BONUS)
    public Queue queue2() {
        return new Queue(ORDER_SEND_BONUS, true);
    }

    @Bean(TEST_QUEUE)
    public Queue queue3() {
        return new Queue(TEST_QUEUE, true, true,true);
    }

    @Bean
    public Exchange syncOrderExchange(){
        return ExchangeBuilder.topicExchange(exchangeSyncOrderName).durable(true).build();
    }

    @Bean
    public Queue syncOrderQueue(){
        return QueueBuilder.durable(queueSyncOrderName).build();
    }

    //绑定队列和交换机
    @Bean
    public Binding bindSyncOrderQueueExchange(){
        return BindingBuilder.bind(syncOrderQueue()).to(syncOrderExchange()).with(exchangeSyncOrderRoutingKey).noargs();
    }

    @Bean
    public Exchange fanoutBonusExchange(){
        return ExchangeBuilder.fanoutExchange(exchangeBonusName).durable(true).build();
    }

    @Bean
    public Exchange inventoryExchange(){
        return ExchangeBuilder.topicExchange(exchangeInventoryName).durable(true).build();
    }

    @Bean
    public Queue inventoryQueue(){
        return QueueBuilder.durable(queueInventoryName).build();
    }

    @Bean
    public Binding bindInventoryQueueExchange(){
        return BindingBuilder.bind(inventoryQueue()).to(inventoryExchange()).with(exchangeInventoryRoutingKey).noargs();
    }

    @Bean
    public Exchange thirdWriteOffExchange(){
        return ExchangeBuilder.topicExchange(exchangeThirdWriteOffName).durable(true).build();
    }

    @Bean
    public Queue thirdWriteOffQueue(){
        return QueueBuilder.durable(queueThirdWriteOffOrderName).build();
    }

    @Bean
    public Binding thirdWriteOffQueueExchange(){
        return BindingBuilder.bind(thirdWriteOffQueue()).to(thirdWriteOffExchange()).with(exchangeThirdWriteOffOrderRoutingKey).noargs();
    }

    @Bean
    public Exchange finishInventoryExchange(){
        return ExchangeBuilder.topicExchange(exchangeFinishInventoryName).durable(true).build();
    }

    @Bean
    public Queue finishInventoryQueue(){
        return QueueBuilder.durable(queueFinishInventoryName).build();
    }

    @Bean
    public Binding bindFinishInventoryQueueExchange(){
        return BindingBuilder.bind(finishInventoryQueue()).to(finishInventoryExchange()).with(exchangeFinishInventoryRoutingKey).noargs();
    }

}
