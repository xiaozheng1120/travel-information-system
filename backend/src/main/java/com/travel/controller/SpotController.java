package com.travel.controller;

import com.travel.service.SpotService;
import com.travel.entity.Spot;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/spot")
public class SpotController {

    @Resource
    private SpotService spotService;

    // 分页接口（后台管理）
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                   Spot spot){
        return spotService.getSpotPage(pageNum,pageSize,spot);
    }

    // 新增景点
    @PostMapping("/add")
    public Boolean add(@RequestBody Spot spot){
        return spotService.addSpot(spot);
    }

    // 修改景点
    @PutMapping("/update")
    public Boolean update(@RequestBody Spot spot){
        return spotService.updateSpot(spot);
    }

    // 删除景点
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return spotService.deleteSpot(id);
    }

    // 查询全部景点（前台页面）
    @GetMapping("/list")
    public List<Spot> list(){
        return spotService.getAllSpot();
    }

    // 统计景点总数，后台首页卡片
    @GetMapping("/count")
    public Integer getSpotCount(){
        return spotService.getSpotCount();
    }

    // 详情页接口 【保留你写的 /getById/{id}，不用改成/{id}，前端接口地址对应这个路径就行】
    @GetMapping("/getById/{id}")
    public Spot getById(@PathVariable Integer id){
        return spotService.getSpotById(id);
    }
}