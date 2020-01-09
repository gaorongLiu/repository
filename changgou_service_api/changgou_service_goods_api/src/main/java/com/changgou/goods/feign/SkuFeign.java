package com.changgou.goods.feign;

import com.changgou.common.entity.Result;
import com.changgou.goods.api.PageResult;
import com.changgou.goods.pojo.Sku;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "goods")
public interface SkuFeign {

    @GetMapping("/sku/spu/{spuId}")
    List<Sku> findSkuListBySpuId(@PathVariable("spuId") String spuId);

    @GetMapping("/sku/spu/{spuId}/{page}")
    PageResult<Sku> findSkuPageBySpuId(@PathVariable("spuId") String spuId, @PathVariable("page") Integer page);

    @GetMapping("/sku/{id}")
     Result<Sku> findById(@PathVariable String id);

    @PostMapping("/sku/decr/count")
    Result decrCount(@RequestParam("username") String username);

    @RequestMapping("/sku/resumeStockNum")
    public Result resumeStockNum(@RequestParam("skuId") String skuId,@RequestParam("num")Integer num);
}
