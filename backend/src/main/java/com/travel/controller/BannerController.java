package com.travel.controller;

import com.travel.entity.Banner;
import com.travel.service.BannerService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/banner")
public class BannerController {

    @Resource
    private BannerService bannerService;

    //分页查询轮播
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "8") Integer pageSize,
                                   Banner banner){
        return bannerService.getBannerPage(pageNum,pageSize,banner);
    }

    //新增轮播
    @PostMapping("/add")
    public Boolean add(@RequestBody Banner banner){
        //后台新增banner默认isFixed=0，代表可编辑删除
        banner.setIsFixed(0);
        return bannerService.addBanner(banner);
    }

    //修改轮播
    @PutMapping("/update")
    public Boolean update(@RequestBody Banner banner){
        return bannerService.updateBanner(banner);
    }

    //删除轮播
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return bannerService.deleteBanner(id);
    }
}
