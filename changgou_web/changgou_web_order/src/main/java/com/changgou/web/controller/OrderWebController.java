package com.changgou.web.controller;

import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.order.feign.CartFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/wcart")
public class OrderWebController {
    @Autowired
    private CartFeign cartFeign;

    //查询
    @GetMapping("/list")
    public String list(Model model) {
        Map map = cartFeign.list();
        model.addAttribute("items", map);
        return "cart";
    }


    //添加
    @GetMapping("/add")
    @ResponseBody
    public Result<Map> add(String id, Integer num,Integer type) {
        cartFeign.addCart(id, num,type);
        Map map = cartFeign.list();
        return new Result<>(true, StatusCode.OK, "添加购物车成功", map);
    }
}