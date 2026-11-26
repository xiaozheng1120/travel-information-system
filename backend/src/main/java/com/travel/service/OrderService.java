package com.travel.service;

import com.travel.entity.Order;
import java.util.Map;

public interface OrderService {
    Boolean addOrder(Order order);
    Boolean updateOrder(Order order);
    Boolean deleteOrder(Integer id);
    Map<String,Object> getOrderPage(Integer pageNum, Integer pageSize, Order order);

    // 新增：根据 id 查询订单详情（前台订单详情页使用）
    Order getOrderById(Integer id);

    // 新增取消订单方法
    Boolean cancelOrder(Integer id);
    // 新增订单核销方法
    Boolean verifyOrder(Integer id);
}
