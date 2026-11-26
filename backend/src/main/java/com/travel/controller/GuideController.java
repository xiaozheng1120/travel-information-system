package com.travel.controller;

import com.travel.entity.Guide;
import com.travel.service.GuideService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/guide")
public class GuideController {

    @Resource
    private GuideService guideService;

    //分页查询
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   Guide guide){
        return guideService.getGuidePage(pageNum,pageSize,guide);
    }

    //新增攻略
    @PostMapping("/add")
    public Boolean add(@RequestBody Guide guide){
        return guideService.addGuide(guide);
    }

    //修改攻略
    @PutMapping("/update")
    public Boolean update(@RequestBody Guide guide){
        return guideService.updateGuide(guide);
    }

    //删除攻略
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return guideService.deleteGuide(id);
    }

    //攻略详情接口
    @GetMapping("/detail/{id}")
    public Guide getDetail(@PathVariable Integer id){
        return guideService.getGuideById(id);
    }
}