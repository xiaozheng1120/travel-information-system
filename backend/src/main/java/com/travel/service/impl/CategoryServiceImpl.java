package com.travel.service.impl;

import com.travel.entity.Category;
import com.travel.mapper.CategoryMapper;
import com.travel.service.CategoryService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public Map<String, Object> getCategoryPage(Integer pageNum, Integer pageSize, Category category) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum - 1) * pageSize;
        List<Category> list = categoryMapper.selectCategoryList(category, start, pageSize);
        int total = categoryMapper.countCategory(category);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    @Override
    public boolean addCategory(Category category) {
        return categoryMapper.insertCategory(category) > 0;
    }

    @Override
    public boolean updateCategory(Category category) {
        return categoryMapper.updateCategory(category) > 0;
    }

    @Override
    public boolean deleteCategory(Integer id) {
        return categoryMapper.deleteCategoryById(id) > 0;
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryMapper.selectAllCategory();
    }

    //新增：实现下拉查询启用分类的方法
    @Override
    public List<Category> getEnableAllCategory() {
        return categoryMapper.selectEnableAllCategory();
    }
}
