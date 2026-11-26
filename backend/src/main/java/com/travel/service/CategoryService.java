package com.travel.service;

import com.travel.entity.Category;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    Map<String,Object> getCategoryPage(Integer pageNum, Integer pageSize, Category category);
    boolean addCategory(Category category);
    boolean updateCategory(Category category);
    boolean deleteCategory(Integer id);
    List<Category> getAllCategory();

    // 新增：景点下拉框，只获取启用的分类
    List<Category> getEnableAllCategory();
}
