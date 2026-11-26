package com.travel.mapper;

import com.travel.entity.Order;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface OrderMapper {
    int insertOrder(Order order);
    int updateOrder(Order order);
    int deleteOrder(Integer id);

    // 分页列表
    List<Order> selectOrderList(@Param("order") Order order, @Param("start") Integer start, @Param("pageSize") Integer pageSize);
    // 统计总数
    int countOrder(@Param("order") Order order);

    // 新增：根据id查询订单（给取消订单功能使用）
    Order selectById(Integer id);
}
