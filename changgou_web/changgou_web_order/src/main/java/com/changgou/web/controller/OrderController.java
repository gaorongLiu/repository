package com.changgou.web.controller;

import com.changgou.common.entity.Result;
import com.changgou.order.feign.CartFeign;
import com.changgou.order.feign.OrderFeign;
import com.changgou.order.pojo.Order;
import com.changgou.order.pojo.OrderItem;
import com.changgou.user.feign.AddressFeign;
import com.changgou.user.pojo.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
//同源策略
@CrossOrigin
@RequestMapping("/worder")
public class OrderController {
    @Autowired
    private AddressFeign addressFeign;
    @Autowired
    private OrderFeign orderFeign;
    @Autowired
    private CartFeign cartFeign;

    @RequestMapping("/ready/order")
    public String readyOrder(Model model) {
        List<Address> addressList = addressFeign.list().getData();
        //默认的地址
        for (Address address : addressList) {
            if (address.getIsDefault().equals("1")) {
                model.addAttribute("deAddr", address);
                break;
            }
        }
        model.addAttribute("address", addressList);

        Map map = cartFeign.list();
        List<OrderItem> orderItemList = (List<OrderItem>) map.get("orderItemList");
        Integer totalMoney = (Integer) map.get("totalMoney");
        Integer totalNum = (Integer) map.get("totalNum");

        model.addAttribute("carts", orderItemList);
        model.addAttribute("totalMoney", totalMoney);
        model.addAttribute("totalNum", totalNum);
        return "order";
    }

    /**
     *添加方法
     * @param order
     * @return
     */
    @PostMapping(value = "/add")
    @ResponseBody
    public Result add(@RequestBody Order order) {
        Result result = orderFeign.add(order);

        return result;
    }
    @GetMapping("/toPage")
    public String toPayPage(String orderId,Model model){
        Order data = orderFeign.findById(orderId).getData();
    model.addAttribute("payMoney",data.getPayMoney());
    model.addAttribute("orderId",orderId);
        return "pay";
    }
}
