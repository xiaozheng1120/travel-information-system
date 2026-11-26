package com.travel.controller;

import com.travel.mapper.GuideMapper;
import com.travel.mapper.HotelMapper;
import com.travel.mapper.SpotMapper;
import com.travel.mapper.TicketMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索联想接口（前台首页、景点、攻略、住宿、门票 5 个页面的搜索框使用）
 *
 * GET /api/suggest/list?type=spot&keyword=杭
 * type 取值：spot（景点名称）、spotArea（景点地区）、guide（攻略标题）、
 *           guideAuthor（攻略作者）、hotel（住宿名称）、ticket（门票名称）
 */
@RestController
@RequestMapping("/api/suggest")
public class SuggestController {

    @Resource
    private SpotMapper spotMapper;

    @Resource
    private GuideMapper guideMapper;

    @Resource
    private HotelMapper hotelMapper;

    @Resource
    private TicketMapper ticketMapper;

    @GetMapping("/list")
    public List<String> list(@RequestParam(required = false, defaultValue = "spot") String type,
                             @RequestParam(required = false, defaultValue = "") String keyword) {
        String kw = keyword == null ? "" : keyword.trim();
        List<String> result;
        switch (type == null ? "" : type) {
            case "spotArea":
                result = spotMapper.selectAreasByKeyword(kw);
                break;
            case "guide":
                result = guideMapper.selectTitlesByKeyword(kw);
                break;
            case "guideAuthor":
                result = guideMapper.selectAuthorsByKeyword(kw);
                break;
            case "hotel":
                result = hotelMapper.selectNamesByKeyword(kw);
                break;
            case "ticket":
                result = ticketMapper.selectNamesByKeyword(kw);
                break;
            case "spot":
            default:
                result = spotMapper.selectNamesByKeyword(kw);
                break;
        }
        return clean(result);
    }

    /** 去掉 null、空串和重复项，保证前端拿到的都是能显示的联想词 */
    private List<String> clean(List<String> list) {
        List<String> result = new ArrayList<>();
        if (list == null) {
            return result;
        }
        for (String item : list) {
            if (item == null) {
                continue;
            }
            String text = item.trim();
            if (text.isEmpty() || result.contains(text)) {
                continue;
            }
            result.add(text);
        }
        return result;
    }
}
