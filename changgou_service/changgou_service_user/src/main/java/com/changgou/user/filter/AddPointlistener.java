package com.changgou.user.filter;

import com.alibaba.fastjson.JSON;
import com.changgou.order.pojo.Task;
import com.changgou.user.config.RabbitMQConfig;
import com.changgou.user.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class AddPointlistener {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.CG_BUYING_ADDPOINT)
    public void receiveMessage(String message) {
        Task task = JSON.parseObject(message, Task.class);

        if (task == null || StringUtils.isEmpty(task.getRequestBody())) {
            return;
        }
        //判断redis中是否存在内容
        Object o = redisTemplate.boundValueOps(task.getId()).get();
        if (o != null) {
            return;
        }
        //更新用户积分
        int result = userService.updateUserPoints(task);
        if (result > 0) {
            return;
        }
        //返回通知
        rabbitTemplate.convertAndSend(RabbitMQConfig.EX_BUYING_ADDPOINTUSER,RabbitMQConfig.CG_BUYING_FINISHADDPOINT_KEY,JSON.toJSONString(task));
        System.out.println("用户服务向完成添加积分队列发送了一条消息");    }
}
