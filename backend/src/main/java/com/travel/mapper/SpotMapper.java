package com.travel.mapper;

import com.travel.entity.Spot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SpotMapper {
    // 分页查询（增加start、pageSize参数，多参数必须加@Param）
    List<Spot> selectSpotList(@Param("spot") Spot spot,
                              @Param("start") Integer start,
                              @Param("pageSize") Integer pageSize);
    // 查询总条数（分页用）
    int countSpot(@Param("spot") Spot spot);
    // 新增
    int insertSpot(Spot spot);
    // 修改
    int updateSpot(Spot spot);
    // 删除
    int deleteSpotById(Integer id);
    // 查询全部（前台首页用）
    List<Spot> selectAllSpot();

    // 新增：后台首页统计全部景点数量，不带筛选条件
    Integer countAllSpot();

    // 新增：根据id查询景点（详情页）
    Spot selectSpotById(Integer id);

    // 新增：搜索联想，按名称模糊匹配，最多返回 10 条
    List<String> selectNamesByKeyword(@Param("keyword") String keyword);

    // 新增：搜索联想，按地区模糊匹配（去重），最多返回 10 条
    List<String> selectAreasByKeyword(@Param("keyword") String keyword);
}