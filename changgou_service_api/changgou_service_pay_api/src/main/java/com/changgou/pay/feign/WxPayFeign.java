package com.changgou.pay.feign;

import com.changgou.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("pay")
public interface WxPayFeign {
    @GetMapping("/wepay/nativepay")
    Result nativePay(@RequestParam("orderId") String orderId, @RequestParam("money") Integer money );

    @PutMapping("/wepay/close/{orderId}")
    Result closeOrder(@PathVariable String orderId);
    /**查询微信订单支付状态*/
    @GetMapping("/wepay/query/{orderId}")
    Result queryOrder(@PathVariable String orderId);
}
