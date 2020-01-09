package com.changgou.order.feign;

import com.changgou.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "order")
public interface CartFeign {

    @GetMapping("/cart/add")
    public Result addCart(@RequestParam("skuId") String skuId, @RequestParam("num") Integer num,@RequestParam("type") Integer type);

    @GetMapping("/cart/list")
    public Map list();
}
