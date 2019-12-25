package com.changgou.goods.feign;

import com.changgou.goods.api.PageResult;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "goods")
public interface SkuFeign {

    @GetMapping("/sku/spu/{spuId}")
     List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping("/sku/spu/{spuId}/{page}")
    PageResult<Sku> findSkuPageBySpuId(@PathVariable("spuId") String spuId, @PathVariable("page") Integer page);
}

