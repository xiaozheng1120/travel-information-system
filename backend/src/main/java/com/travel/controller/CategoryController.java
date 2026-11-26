package com.travel.controller;

import com.travel.entity.Category;
import com.travel.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    // 后台分页查询分类
    @GetMapping("/page")
    public Map<String,Object> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "5") Integer pageSize,
                                   Category category){
        return categoryService.getCategoryPage(pageNum,pageSize,category);
    }

    // 获取所有【启用】的分类
    @GetMapping("/list")
    public List<Category> list(){
        return categoryService.getAllCategory();
    }

    // 新增：景点弹窗下拉专用接口
    @GetMapping("/enableAll")
    public List<Category> enableAll(){
        return categoryService.getEnableAllCategory();
    }

    // 新增分类
    @PostMapping("/add")
    public Boolean add(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    // 修改分类
    @PutMapping("/update")
    public Boolean update(@RequestBody Category category){
        return categoryService.updateCategory(category);
    }

    // 删除分类
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable Integer id){
        return categoryService.deleteCategory(id);
    }
}
