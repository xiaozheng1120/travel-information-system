package com.travel.mapper;

import com.travel.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectCategoryList(@Param("category") Category category,
                                      @Param("start") Integer start,
                                      @Param("pageSize") Integer pageSize);

    int countCategory(@Param("category") Category category);

    int insertCategory(Category category);

    int updateCategory(Category category);

    int deleteCategoryById(Integer id);

    List<Category> selectAllCategory();

    // 新增：只查询【启用】的分类，景点新增弹窗下拉框使用
    List<Category> selectEnableAllCategory();
}
