package com.travel.service.impl;

import com.travel.entity.Spot;
import com.travel.mapper.SpotMapper;
import com.travel.service.SpotService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpotServiceImpl implements SpotService {

    @Resource
    private SpotMapper spotMapper;

    @Override
    public Map<String, Object> getSpotPage(Integer pageNum, Integer pageSize, Spot spot) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum-1)*pageSize;
        // 直接传start、pageSize到mapper，不要set进spot对象
        List<Spot> list = spotMapper.selectSpotList(spot, start, pageSize);
        int total = spotMapper.countSpot(spot);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    @Override
    public boolean addSpot(Spot spot) {
        return spotMapper.insertSpot(spot) > 0;
    }

    @Override
    public boolean updateSpot(Spot spot) {
        return spotMapper.updateSpot(spot) >0;
    }

    @Override
    public boolean deleteSpot(Integer id) {
        return spotMapper.deleteSpotById(id)>0;
    }

    @Override
    public List<Spot> getAllSpot() {
        return spotMapper.selectAllSpot();
    }

    // 新增实现：统计全部景点总数（后台首页用，不带查询条件）
    @Override
    public Integer getSpotCount() {
        return spotMapper.countAllSpot();
    }

    // 新增：根据id查询景点，详情页接口实现
    @Override
    public Spot getSpotById(Integer id) {
        return spotMapper.selectSpotById(id);
    }
}