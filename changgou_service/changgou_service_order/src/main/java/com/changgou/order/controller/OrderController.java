package com.changgou.order.controller;

import com.changgou.common.entity.PageResult;
import com.changgou.common.entity.Result;
import com.changgou.common.entity.StatusCode;
import com.changgou.order.config.TokenDecode;
import com.changgou.order.pojo.Order;
import com.changgou.order.service.OrderService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private TokenDecode tokenDecode;
    @Autowired
    private OrderService orderService;

    /**
     * 查询全部数据
     *
     * @return
     */
    @GetMapping
    public Result findAll() {
        List<Order> orderList = orderService.findAll();
        return new Result(true, StatusCode.OK, "查询成功", orderList);
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Order> findById(@PathVariable String id) {
        Order order = orderService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", order);
    }


    /***
     * 新增数据
     * @param order
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Order order) {
        Map<String, String> userInfo = tokenDecode.getUserInfo();
        String username = userInfo.get("username");
        order.setUsername(username);
        String orderid = orderService.add(order);
        return new Result(true, StatusCode.OK, "添加成功", orderid);
    }


    /***
     * 修改数据
     * @param order
     * @param id
     * @return
     */
    @PutMapping(value = "/{id}")
    public Result update(@RequestBody Order order, @PathVariable String id) {
        order.setId(id);
        orderService.update(order);
        return new Result(true, StatusCode.OK, "修改成功");
    }


    /***
     * 根据ID删除品牌数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}")
    public Result delete(@PathVariable String id) {
        orderService.delete(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /***
     * 多条件搜索品牌数据
     * @param searchMap
     * @return
     */
    @PostMapping(value = "/search")
    public Result findList(@RequestBody Map searchMap) {
        List<Order> list = orderService.findList(searchMap);
        return new Result(true, StatusCode.OK, "查询成功", list);
    }


    /***
     * 分页搜索实现
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @PostMapping(value = "/search/{page}/{size}")
    public Result findPage(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Order> pageList = orderService.findPage(searchMap, page, size);
        PageResult pageResult = new PageResult(pageList.getTotal(), pageList.getResult());
        return new Result(true, StatusCode.OK, "查询成功", pageResult);
    }

    /**
     * 批量发货
     * @param orderList
     * @return
     */
    @PostMapping("/batchSend")
    public Result batchSend(@RequestParam("orderList") List<Order> orderList) {
        orderService.batchSend(orderList);
        return new Result(true,StatusCode.OK,"平凉发货成功");
    }

    @PutMapping("/take/{orderId}/{operator}")
    public Result take(@PathVariable("orderId") Integer orderId,@PathVariable("operator")String operator){
        orderService.take(orderId,operator);
        return new Result(true,StatusCode.OK,"宁已确认收货");
    }
}
