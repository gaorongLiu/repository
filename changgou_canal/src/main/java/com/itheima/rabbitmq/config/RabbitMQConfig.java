package com.itheima.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RabbitMQConfig {
    //定义队列名称
    public static final String AD_UPDATE_QUEUE = "ad_update_queue";
    //定义队列名称
    public static final String SEARCH_ADD_QUEUE = "search_add_queue";
    //交换机名称
    private static final String GOODS_UP_EXCHANGE = "goods_up_exchange";

    @Bean
    public Queue adUpdateQueue() {
        return new Queue(AD_UPDATE_QUEUE);
    }

    @Bean
    public Queue searchAddQueue() {
        return new Queue(SEARCH_ADD_QUEUE);
    }

    //声明交换机
    @Bean
    public Exchange goodsUpExchange() {
        return ExchangeBuilder.fanoutExchange("goods_up_exchange").durable(true).build();
    }

    //绑定id
    public Binding AD_UPDATE_QUEUE_BINDING(@Qualifier(AD_UPDATE_QUEUE) Queue queue, @Qualifier(GOODS_UP_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }
}