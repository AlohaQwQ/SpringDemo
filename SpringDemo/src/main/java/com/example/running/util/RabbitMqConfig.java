package com.example.running.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.SerializerMessageConverter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.text.MessageFormat;

/**
 * rabbitmq配置
 *
 * @author chenmeng
 * @date 2022/04/29 15:56
 **/
@Configuration
@ConfigurationProperties("spring.rabbitmq")
public class RabbitMqConfig {

    private static Logger logger = LoggerFactory.getLogger(RabbitMqConfig.class);

    private String host;
    private Integer port;
    private String username;
    private String password;
    private String virtualHost;
    private int messageExpiration;

    @Bean
    public ConnectionFactory zmConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(host);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate zmRabbitTemplate() {
        CachingConnectionFactory connectionFactory = (CachingConnectionFactory) zmConnectionFactory();
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
            logger.info("mq send fail return msg : {}", returned.toString());
            /*logger.info(MessageFormat.format("消息发送ReturnCallback:{0},{1},{2},{3},{4},{5}"
                    , returned.getMessage().toString(), returned.getReplyCode(), returned.getReplyText()
                    , returned.getExchange(), returned.getRoutingKey()));*/
        });
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        return template;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public void setMessageExpiration(int messageExpiration) {
        this.messageExpiration = messageExpiration;
    }

    public int getMessageExpiration() {
        return messageExpiration;
    }
}
