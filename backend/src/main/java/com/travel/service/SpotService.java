package com.travel.service;

import com.travel.entity.Spot;
import java.util.List;
import java.util.Map;

public interface SpotService {
    //分页
    Map<String,Object> getSpotPage(Integer pageNum,Integer pageSize,Spot spot);
    boolean addSpot(Spot spot);
    boolean updateSpot(Spot spot);
    boolean deleteSpot(Integer id);
    List<Spot> getAllSpot();

    //新增：统计景点总数接口
    Integer getSpotCount();

    // 新增：根据id查询景点（详情页使用）
    Spot getSpotById(Integer id);
}