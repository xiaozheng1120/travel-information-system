package com.travel.controller;

import com.travel.entity.Order;
import com.travel.service.OrderService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    //分页查询
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   Order order){
        //新增调试打印，查看前端传过来的order对象
        System.out.println("====前端传入的order对象：" + order);
        return orderService.getOrderPage(pageNum,pageSize,order);
    }

    //新增订单
    @PostMapping("/add")
    public Boolean add(@RequestBody Order order){
        System.out.println("====待保存订单信息：" + order);
        return orderService.addOrder(order);
    }

    // 根据 id 查询订单详情（前台订单详情页使用）
    @GetMapping("/{id}")
    public Order getById(@PathVariable Integer id){
        return orderService.getOrderById(id);
    }

    //修改订单
    @PutMapping("/update")
    public Boolean update(@RequestBody Order order){
        return orderService.updateOrder(order);
    }

    //删除订单
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return orderService.deleteOrder(id);
    }

    // ========== 订单核销接口 ==========
    @PutMapping("/verify")
    public Boolean verifyOrder(@RequestParam Integer id){
        return orderService.verifyOrder(id);
    }

    // ========== 取消订单接口 ==========
    @PutMapping("/cancel")
    public Boolean cancelOrder(@RequestParam Integer id){
        return orderService.cancelOrder(id);
    }
}
