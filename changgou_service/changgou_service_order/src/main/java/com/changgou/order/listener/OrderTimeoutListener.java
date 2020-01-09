package com.changgou.order.listener;

import com.changgou.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderTimeoutListener {
 private OrderService orderService;
    @RabbitListener(queues = "queue.ordertimeout")
    public void closeOrder(String orderId){
        //监听关闭
        try {
            orderService.closeOrder(orderId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
