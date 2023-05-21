package com.example.running.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import java.text.MessageFormat;

/**
 * FileName:   MQConfig
 *
 * @author: Gengzhi
 * @since: 2022/1/2011:11
 * @description:
 */

@Configuration
public class MqBindingConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqBindingConfig.class);

    @Value("${spring.rabbitmq.host}")
    private String host;

    @Value("${spring.rabbitmq.port}")
    private Integer port;

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualhost;

    @Value("${spring.rabbitmq.config.queue}")
    private String queue;

    @Value("${spring.rabbitmq.config.exchange}")
    private String exchange;

    @Value("${spring.rabbitmq.config.routingKey}")
    private String routingKey;

    @Value("${spring.rabbitmq.messageExpiration}")
    String messageExpiration;

    public static final String EXCHANGE_NAME = "zk.exchange.inventory";
    @Value("${spring.rabbitmq.password}")
    public static final String ORDER_BONUS_NAME = "zk.queue.inventory";
    public static final String ROUTING_KEY = "finishinventory.#";//积分状态队列

    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualhost);
        /** 如果要进行消息回调，则这里必须要设置为true */
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }

    @Bean
    @Primary
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate() {
        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) connectionFactory();
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMandatory(true);

        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
        template.setConfirmCallback((data, ack, cause) -> {
            if (!ack) {
                logger.error("消息发送失败!" + cause);
            } else {
                logger.info("消息发送成功,消息ID：" + (data != null ? data.getId() : null));
            }
        });

        connectionFactory.setPublisherReturns(true);
        template.setReturnsCallback(returned -> {
            //logger.info("mq send fail return msg : {}", returned.getMessage().toString());
            logger.info(MessageFormat.format("消息发送ReturnCallback:{0},{1},{2},{3},{4},{5}"
                    , returned.getMessage().toString(), returned.getReplyCode(), returned.getReplyText()
                    , returned.getExchange(), returned.getRoutingKey()));
        });
        template.setMessageConverter(new Jackson2JsonMessageConverter());
//        template.setMessageConverter(new SerializerMessageConverter());
        return template;
    }

    @Bean
    public Queue queue() {
        return new Queue(queue, true, true,true);
    }

    @Bean
    public Exchange exchange(){
        return ExchangeBuilder.topicExchange(exchange).durable(true).build();
    }

    @Bean
    public Binding bindQueueExchange(){
        return BindingBuilder.bind(queue()).to(exchange()).with(routingKey).noargs();
    }
}
