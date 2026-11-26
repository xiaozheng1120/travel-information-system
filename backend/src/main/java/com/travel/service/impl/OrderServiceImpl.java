package com.travel.service.impl;

import com.travel.entity.Order;
import com.travel.mapper.OrderMapper;
import com.travel.mapper.TicketMapper;
import com.travel.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderMapper orderMapper;

    //注入TicketMapper，用来扣减/返还库存
    @Resource
    private TicketMapper ticketMapper;

    @Override
    @Transactional //开启事务，订单保存和库存扣减原子操作
    public Boolean addOrder(Order order) {
        // 新增订单时如果没有指定类型，默认按门票订单处理（保证 orderType 非空）
        if (order.getOrderType() == null) {
            order.setOrderType(1);
        }
        // =========新增这一行：自动设置下单时间=========
        order.setCreateTime(new Date());
        //1.新增订单
        int insertRow = orderMapper.insertOrder(order);
        if(insertRow <= 0){
            return false;
        }
        //2.扣减门票库存（只有门票订单才有库存，酒店住宿订单跳过）
        if (order.getOrderType() == null || order.getOrderType() == 1) {
            int stockRow = ticketMapper.updateStock(order.getTicketId(), order.getCount());
            System.out.println("====库存更新影响行数：" + stockRow);
            // 如果库存更新失败，抛出异常，事务回滚，订单自动删除
            if(stockRow <= 0){
                throw new RuntimeException("库存不足，下单失败");
            }
        }
        return true;
    }

    @Override
    public Order getOrderById(Integer id) {
        return orderMapper.selectById(id);
    }

    @Override
    public Boolean updateOrder(Order order) {
        return orderMapper.updateOrder(order) > 0;
    }

    @Override
    public Boolean deleteOrder(Integer id) {
        return orderMapper.deleteOrder(id) >0;
    }

    @Override
    public Map<String, Object> getOrderPage(Integer pageNum, Integer pageSize, Order order) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum - 1) * pageSize;
        List<Order> list = orderMapper.selectOrderList(order, start, pageSize);
        int total = orderMapper.countOrder(order);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    // ========== 订单核销（待出行1 → 已完成2） ==========
    @Override
    @Transactional
    public Boolean verifyOrder(Integer id) {
        Order order = orderMapper.selectById(id);
        // 订单必须存在并且状态是【待出行1】才允许核销
        if(order == null || !Integer.valueOf(1).equals(order.getStatus())){
            return false;
        }
        // 修改订单状态为已完成 2
        order.setStatus(2);
        return orderMapper.updateOrder(order) > 0;
    }

    // ========== 取消订单，返还库存（待出行1 → 已取消3） ==========
    @Override
    @Transactional
    public Boolean cancelOrder(Integer id) {
        // 查询订单信息
        Order order = orderMapper.selectById(id);
        // 订单不存在 或者 订单不是待出行(1)，直接返回false
        if(order == null || !Integer.valueOf(1).equals(order.getStatus())){
            return false;
        }
        // 修改订单状态为 3（已取消）
        order.setStatus(3);
        orderMapper.updateOrder(order);
        // 库存加回（只有门票订单需要返还库存，酒店住宿订单跳过）
        if (order.getOrderType() == null || order.getOrderType() == 1) {
            ticketMapper.addStock(order.getTicketId(), order.getCount());
        }
        return true;
    }
}
