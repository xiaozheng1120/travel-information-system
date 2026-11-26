package com.travel.controller;

import com.travel.entity.Favorite;
import com.travel.service.FavoriteService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Resource
    private FavoriteService favoriteService;

    // 当前用户的全部收藏
    @GetMapping("/list")
    public List<Favorite> list(@RequestParam Integer userId) {
        return favoriteService.listByUser(userId);
    }

    // 是否已收藏
    @GetMapping("/check")
    public Boolean check(@RequestParam Integer userId,
                         @RequestParam String targetType,
                         @RequestParam Integer targetId) {
        return favoriteService.exists(userId, targetType, targetId);
    }

    // 添加收藏
    @PostMapping("/add")
    public Boolean add(@RequestBody Favorite favorite) {
        return favoriteService.add(favorite);
    }

    // 取消收藏（按用户 + 类型 + 目标 id）
    @DeleteMapping("/remove")
    public Boolean remove(@RequestParam Integer userId,
                          @RequestParam String targetType,
                          @RequestParam Integer targetId) {
        return favoriteService.remove(userId, targetType, targetId);
    }

    // 取消收藏（按收藏记录 id）
    @DeleteMapping("/remove/{id}")
    public Boolean removeById(@PathVariable Integer id) {
        return favoriteService.removeById(id);
    }
}
