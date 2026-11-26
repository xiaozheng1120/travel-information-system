package com.travel.controller;

import com.travel.entity.Hotel;
import com.travel.service.HotelService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/hotel")
public class HotelController {

    @Resource
    private HotelService hotelService;

    //分页查询
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   Hotel hotel){
        return hotelService.getHotelPage(pageNum,pageSize,hotel);
    }

    //新增
    @PostMapping("/add")
    public Boolean add(@RequestBody Hotel hotel){
        return hotelService.addHotel(hotel);
    }

    //修改
    @PutMapping("/update")
    public Boolean update(@RequestBody Hotel hotel){
        return hotelService.updateHotel(hotel);
    }

    //删除
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return hotelService.deleteHotel(id);
    }

    //根据id查询酒店详情（你原本写好的，保留，不用改动）
    @GetMapping("/{id}")
    public Hotel getById(@PathVariable Integer id){
        return hotelService.getHotelById(id);
    }
}