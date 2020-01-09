package com.changgou.order.listener;

import com.changgou.order.service.OrderService;
import com.changgou.task.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderTackListener {
@Autowired
private OrderService orderService;
    @RabbitListener(queues = RabbitMQConfig.ORDER_TACK)
    public void tacklsn(String message){
        System.out.println(message);
        orderService.autoTack(); //自动确认收货
    }
}
