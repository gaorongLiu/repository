package com.changgou.goods.feign;

import com.changgou.common.entity.Result;
import com.changgou.goods.pojo.Category;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "goods")
public interface CategoryFeign {

    @GetMapping("/category/{id}")
    public Result<Category> findById(@PathVariable("id") Integer id);

    @GetMapping("/sku/spu/{spuId}")
    public List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);
}
