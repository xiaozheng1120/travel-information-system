package com.travel.service;

import com.travel.entity.Banner;
import java.util.Map;

public interface BannerService {
    Boolean addBanner(Banner banner);
    Boolean updateBanner(Banner banner);
    Boolean deleteBanner(Integer id);
    Map<String,Object> getBannerPage(Integer pageNum, Integer pageSize, Banner banner);
}
