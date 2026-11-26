package com.travel.mapper;

import com.travel.entity.Hotel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface HotelMapper {
    //分页查询
    List<Hotel> selectHotelList(@Param("hotel") Hotel hotel,
                                @Param("start") Integer start,
                                @Param("pageSize") Integer pageSize);
    //统计总数
    int countHotel(@Param("hotel") Hotel hotel);
    //新增
    int insertHotel(Hotel hotel);
    //修改
    int updateHotel(Hotel hotel);
    //删除
    int deleteHotelById(Integer id);
    //根据id查询
    Hotel selectHotelById(Integer id);

    // 新增：搜索联想，按住宿名称模糊匹配，最多返回 10 条
    List<String> selectNamesByKeyword(@Param("keyword") String keyword);
}