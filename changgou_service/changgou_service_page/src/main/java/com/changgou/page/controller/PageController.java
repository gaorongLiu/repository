package com.changgou.page.controller;

import com.changgou.common.entity.Result;
import com.changgou.goods.feign.SpuFeign;
import com.changgou.goods.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/page")
public class PageController {
    @Autowired
    private SpuFeign spuFeign;

    @GetMapping("/{spuId}")
    public void test(@PathVariable("spuId") String spuId){
        Result<Spu> spuById = spuFeign.findSpuById(spuId);
        System.out.println(spuById);
    }
}
