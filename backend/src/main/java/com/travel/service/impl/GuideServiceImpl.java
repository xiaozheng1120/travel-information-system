package com.travel.service.impl;

import com.travel.entity.Guide;
import com.travel.mapper.GuideMapper;
import com.travel.service.GuideService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GuideServiceImpl implements GuideService {

    @Resource
    private GuideMapper guideMapper;

    @Override
    public Map<String, Object> getGuidePage(Integer pageNum, Integer pageSize, Guide guide) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum -1)*pageSize;
        List<Guide> list = guideMapper.selectGuideList(guide, start, pageSize);
        int total = guideMapper.countGuide(guide);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    @Override
    public boolean addGuide(Guide guide) {
        guide.setDate(new Date());
        // 新增攻略默认浏览量为 0、评分为 0，避免数据库非空约束报错
        if (guide.getViews() == null) guide.setViews(0);
        if (guide.getRating() == null) guide.setRating(0.0);
        return guideMapper.insertGuide(guide) >0;
    }

    @Override
    public boolean updateGuide(Guide guide) {
        guide.setDate(new Date());
        if (guide.getRating() == null) guide.setRating(0.0);
        return guideMapper.updateGuide(guide) >0;
    }

    @Override
    public boolean deleteGuide(Integer id) {
        return guideMapper.deleteGuideById(id)>0;
    }

    @Override
    public Guide getGuideById(Integer id) {
        Guide guide = guideMapper.selectGuideById(id);
        // 打开详情页浏览量 +1，并把最新数值返回给前端展示
        if (guide != null) {
            guideMapper.increaseViews(id);
            int current = guide.getViews() == null ? 0 : guide.getViews();
            guide.setViews(current + 1);
        }
        return guide;
    }
}