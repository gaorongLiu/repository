package com.changgou.order.service.impl;

import com.alibaba.fastjson.JSON;
import com.changgou.common.entity.Result;
import com.changgou.common.exception.ExceptionCast;
import com.changgou.common.model.response.order.OrderCode;
import com.changgou.common.util.IdWorker;
import com.changgou.goods.feign.SkuFeign;
import com.changgou.order.config.RabbitMQConfig;
import com.changgou.order.dao.*;
import com.changgou.order.pojo.*;
import com.changgou.order.service.CartService;
import com.changgou.order.service.OrderService;
import com.changgou.pay.feign.WxPayFeign;
import com.changgou.user.feign.UserFeign;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartService cartService;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private WxPayFeign wxPayFeign;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Override
    @Transactional
    public void updatePayStatus(String orderId, String transactionId) {

        //1.查询订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order != null && "0".equals(order.getPayStatus())) {
            //2.修改订单的支付状态
            order.setPayStatus("1");
            order.setOrderStatus("1");
            order.setUpdateTime(new Date());
            order.setPayTime(new Date());
            order.setTransactionId(transactionId); //微信返回的交易流水号
            orderMapper.updateByPrimaryKeySelective(order);

            //3.记录订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("交易流水号:" + transactionId);
            orderLog.setOrderId(orderId);
            orderLogMapper.insert(orderLog);
        }
    }

    /**
     * 查询全部列表
     *
     * @return
     */
    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    /**
     * 根据ID查询
     *
     * @param id
     * @return
     */
    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }


    /**
     * 增加
     *
     * @param order
     */
    @Override
    public String add(Order order) {
        //1.获取购物车的相关数据(redis)
        Map cartMap = cartService.list(order.getUsername());
        List<OrderItem> orderItemList = (List<OrderItem>) cartMap.get("orderItemList");

        //2.统计计算:总金额,总数量
        //3.填充订单数据并保存到tb_order
        order.setTotalNum((Integer) cartMap.get("totalNum"));
        order.setTotalMoney((Integer) cartMap.get("totalMoney"));
        order.setPayMoney((Integer) cartMap.get("totalMoney"));
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setBuyerRate("0"); // 0:未评价  1:已评价
        order.setSourceType("1"); //1:WEB
        order.setOrderStatus("0"); //0:未完成 1:已完成 2:已退货
        order.setPayStatus("0"); //0:未支付 1:已支付
        order.setConsignStatus("0"); //0:未发货 1:已发货
        String orderId = idWorker.nextId() + "";
        order.setId(orderId);
        orderMapper.insertSelective(order);

        //4.填充订单项数据并保存到tb_order_item
        for (OrderItem orderItem : orderItemList) {
            orderItem.setId(idWorker.nextId() + "");
            orderItem.setIsReturn("0"); //0:未退货 1:已退货
            orderItem.setOrderId(orderId);
            orderItemMapper.insertSelective(orderItem);
        }

        //扣减库存并增加销量
        skuFeign.decrCount(order.getUsername());
//添加积分
        userFeign.pointAdd(10);
        //添加任务数据
        System.out.println("向订单数据库中的任务表去添加任务数据");
        Task task = new Task();
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        task.setMqExchange(RabbitMQConfig.EX_BUYING_ADDPOINTUSER);
        task.setMqRoutingkey(RabbitMQConfig.CG_BUYING_ADDPOINT_KEY);

        Map map = new HashMap();
        map.put("username", order.getUsername());
        map.put("orderId", orderId);
        map.put("point", order.getPayMoney());
        task.setRequestBody(JSON.toJSONString(map));
        taskMapper.insertSelective(task);
        //5.删除购物车数据(redis)
        redisTemplate.delete("cart_" + order.getUsername());
        rabbitTemplate.convertAndSend("", "queue.ordercreate", orderId);
        return orderId;
    }


    /**
     * 修改
     *
     * @param order
     */
    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 删除
     *
     * @param id
     */
    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }


    /**
     * 条件查询
     *
     * @param searchMap
     * @return
     */
    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param size
     * @return
     */
    @Override
    public Page<Order> findPage(int page, int size) {
        PageHelper.startPage(page, size);
        return (Page<Order>) orderMapper.selectAll();
    }

    /**
     * 条件+分页查询
     *
     * @param searchMap 查询条件
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @Override
    public Page<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Order>) orderMapper.selectByExample(example);
    }

    /**
     * 关闭订单
     *
     * @param orderId
     */
    @Override
    public void closeOrder(String orderId) {
        System.out.println("orderController关闭订单" + orderId);
        // 1）根据id查询订单信息，判断订单是否存在，订单支付状态是否为未支付
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在！");
        }
        if (!"0".equals(order.getOrderStatus())) {
            System.out.println("此订单不用关闭");
            return;
        }
        System.out.println("关闭订单通过校验：" + orderId);

        //2）基于微信查询订单支付状态
        Result result = wxPayFeign.queryOrder(orderId);
        Map data = (Map) result.getData();
        if ("SUCCESS".equals(data.get("trade_state"))) {
            //如果为success，则修改订单状态
            updatePayStatus(orderId, (String) data.get("transaction_id"));
        }

        //2.2）如果为未支付，则修改订单，新增日志，恢复库存，关闭订单
        if ("NOTPAY".equals(data.get("trade_state"))) {
            System.out.println("订单关闭");
            order.setCloseTime(new Date());//关闭时间
            order.setOrderStatus("4");//关闭状态
            orderMapper.updateByPrimaryKeySelective(order);//更新订单
            //记录订单变动日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");//设置id
            orderLog.setOperater("system");
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("4");
            orderLog.setOrderId(order.getId());
            orderLogMapper.insert(orderLog);
            //恢复库存和销量
            OrderItem _orderItem = new OrderItem();
            _orderItem.setOrderId(orderId);
            List<OrderItem> orderItemList = orderItemMapper.select(_orderItem);
            for (OrderItem orderItem : orderItemList) {
                skuFeign.resumeStockNum(orderItem.getSkuId(), orderItem.getNum());
            }
            wxPayFeign.closeOrder(orderId);
        }
    }

    /**
     * 批量发货
     *
     * @param orderList
     */
    @Override
    public void batchSend(List<Order> orderList) {

        for (Order order : orderList) {
            if (order.getId() == null) {
                ExceptionCast.cast(OrderCode.ORDERID_NOT_EXIST);
            }
            if (order.getShippingCode().isEmpty() || order.getShippingName().isEmpty()) {
                ExceptionCast.cast(OrderCode.SHUPPINGCODE_OR_NAME_EMPTY);
            }
        }
        //进行订单状态的校验
        for (Order order : orderList) {
            Order order1 = orderMapper.selectByPrimaryKey(order.getId());
            if (!"0".equals(order1.getConsignStatus()) || !"1".equals(order1.getOrderStatus())) {
                throw new RuntimeException("订单状态不合法");
            }
        }
        //修改订单的状态为已发货
        for (Order order : orderList) {
            order.setOrderStatus("2"); //已发货
            order.setConsignStatus("1");//已发货
            order.setConsignTime(new Date());
            order.setUpdateTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);

            //记录订单日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperateTime(new Date());
            orderLog.setOperater("admin");
            orderLog.setOrderStatus("2");
            orderLog.setConsignStatus("1");
            orderLog.setOrderId(order.getId());
            orderLogMapper.insertSelective(orderLog);
        }
    }

    /**
     * 手动确认收货
     *
     * @param orderId
     * @param operator
     */
    @Override
    public void take(Integer orderId, String operator) {
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if (order.getId().isEmpty()) {
            ExceptionCast.cast(OrderCode.ORDERID_NOT_EXIST);
        }
        if (!"1".equals(order.getConsignStatus())) {
            ExceptionCast.cast(OrderCode.ORDER_NOT_SEND);
        }
        order.setConsignStatus("2"); //已送达
        order.setOrderStatus("3");//已完成
        order.setUpdateTime(new Date());
        order.setEndTime(new Date());//交易结束
        orderMapper.updateByPrimaryKeySelective(order); //记录订单变动日志
        OrderLog orderLog = new OrderLog();
        orderLog.setId(idWorker.nextId() + "");
        orderLog.setOperateTime(new Date());//当前日期
        orderLog.setOperater(operator);//系统？管理员？用户？
        orderLog.setOrderId(order.getId());
        orderLogMapper.insertSelective(orderLog);
    }

    /**
     * 到期默认自动收货
     */
    @Override
    @Transactional
    public void autoTack() {
        /*1）从订单配置表中获取订单自动确认期限
            2）得到当前日期向前数（订单自动确认期限）天。作为过期时间节点
            3）从订单表中获取过期订单（发货时间小于过期时间，且为未确认收货状态）
            4）循环批量处理，执行确认收货*/
        OrderConfig orderConfig = orderConfigMapper.selectByPrimaryKey(1);
        //当前日期
        LocalDate now = LocalDate.now();
        //
        LocalDate date = now.plusDays(-orderConfig.getTakeTimeout());
        System.out.println(date);
        //按条件查询过期订单
        Example example=new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("consignTime",date);
        criteria.andEqualTo("orderStatus","2");

        List<Order> orders = orderMapper.selectByExample(example);
        for (Order order : orders) {
            String id = order.getId();
            Integer integer = Integer.valueOf(id);
            take(integer,"system");
        }
    }

    /**
     * 构建查询对象
     *
     * @param searchMap
     * @return
     */
    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (searchMap != null) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andEqualTo("payType", searchMap.get("payType"));
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andEqualTo("sourceType", searchMap.get("sourceType"));
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andEqualTo("orderStatus", searchMap.get("orderStatus"));
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andEqualTo("payStatus", searchMap.get("payStatus"));
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
