package com.changgou.user.feign;

import com.changgou.common.entity.Result;
import com.changgou.user.pojo.Address;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user")
public interface AddressFeign {

    @GetMapping("/address/list")
    Result<List<Address>> list();
}
