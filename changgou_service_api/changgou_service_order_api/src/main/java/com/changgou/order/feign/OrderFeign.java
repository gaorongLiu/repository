package com.changgou.order.feign;

import com.changgou.common.entity.Result;
import com.changgou.order.pojo.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order")
public interface OrderFeign {

    @PostMapping("/order")
     Result add(@RequestBody Order order);
    @GetMapping("/order/{id}")
     Result<Order> findById(@PathVariable String id);
}
