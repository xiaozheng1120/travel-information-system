package com.travel.controller;

import com.travel.entity.Ticket;
import com.travel.service.TicketService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    @Resource
    private TicketService ticketService;

    //分页查询
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   Ticket ticket){
        return ticketService.getTicketPage(pageNum,pageSize,ticket);
    }

    //新增
    @PostMapping("/add")
    public Boolean add(@RequestBody Ticket ticket){
        return ticketService.addTicket(ticket);
    }

    //修改
    @PutMapping("/update")
    public Boolean update(@RequestBody Ticket ticket){
        return ticketService.updateTicket(ticket);
    }

    //删除 【修改点：改为RequestParam接收url? id参数，适配前端】
    @DeleteMapping("/delete")
    public Boolean delete(@RequestParam Integer id){
        return ticketService.deleteTicket(id);
    }

    //根据id查询
    @GetMapping("/getById")
    public Ticket getById(@RequestParam Integer id){
        return ticketService.getTicketById(id);
    }
}
