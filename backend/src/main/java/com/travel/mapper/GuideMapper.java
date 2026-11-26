package com.travel.mapper;

import com.travel.entity.Guide;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GuideMapper {
    //分页查询
    List<Guide> selectGuideList(@Param("guide") Guide guide,
                                @Param("start") Integer start,
                                @Param("pageSize") Integer pageSize);
    //统计总数
    int countGuide(@Param("guide") Guide guide);
    //新增
    int insertGuide(Guide guide);
    //修改
    int updateGuide(Guide guide);
    //删除
    int deleteGuideById(Integer id);
    //根据id查详情
    Guide selectGuideById(Integer id);

    // 新增：详情页浏览量 +1
    int increaseViews(Integer id);

    // 新增：搜索联想，按攻略标题模糊匹配，最多返回 10 条
    List<String> selectTitlesByKeyword(@Param("keyword") String keyword);

    // 新增：搜索联想，按作者模糊匹配（去重），最多返回 10 条
    List<String> selectAuthorsByKeyword(@Param("keyword") String keyword);
}