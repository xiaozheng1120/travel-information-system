package com.travel.service;

import com.travel.entity.Guide;
import java.util.Map;

public interface GuideService {
    Map<String,Object> getGuidePage(Integer pageNum, Integer pageSize, Guide guide);
    boolean addGuide(Guide guide);
    boolean updateGuide(Guide guide);
    boolean deleteGuide(Integer id);
    Guide getGuideById(Integer id);
}