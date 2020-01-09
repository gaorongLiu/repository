package com.changgou.task.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;

public class RabbitMQConfig {
    public static final String ORDER_TACK="order_tack";

    @Bean
    public Queue queue(){
        return new Queue(ORDER_TACK);
    }
}
