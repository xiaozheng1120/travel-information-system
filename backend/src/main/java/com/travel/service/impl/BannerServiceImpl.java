package com.travel.service.impl;

import com.travel.entity.Banner;
import com.travel.mapper.BannerMapper;
import com.travel.service.BannerService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BannerServiceImpl implements BannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public Boolean addBanner(Banner banner) {
        int row = bannerMapper.insertBanner(banner);
        return row > 0;
    }

    @Override
    public Boolean updateBanner(Banner banner) {
        int row = bannerMapper.updateBanner(banner);
        return row > 0;
    }

    @Override
    public Boolean deleteBanner(Integer id) {
        int row = bannerMapper.deleteBanner(id);
        return row > 0;
    }

    @Override
    public Map<String, Object> getBannerPage(Integer pageNum, Integer pageSize, Banner banner) {
        int start = (pageNum - 1) * pageSize;
        List<Banner> list = bannerMapper.selectBannerList(banner, start, pageSize);
        int total = bannerMapper.countBanner(banner);
        Map<String,Object> map = new HashMap<>();
        map.put("records", list);
        map.put("total", total);
        map.put("pageNum", pageNum);
        map.put("pageSize", pageSize);
        return map;
    }
}
