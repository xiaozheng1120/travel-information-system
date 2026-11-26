package com.travel.service.impl;

import com.travel.entity.Hotel;
import com.travel.mapper.HotelMapper;
import com.travel.service.HotelService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HotelServiceImpl implements HotelService {

    @Resource
    private HotelMapper hotelMapper;

    @Override
    public Map<String, Object> getHotelPage(Integer pageNum, Integer pageSize, Hotel hotel) {
        Map<String,Object> res = new HashMap<>();
        int start = (pageNum -1)*pageSize;
        List<Hotel> list = hotelMapper.selectHotelList(hotel, start, pageSize);
        int total = hotelMapper.countHotel(hotel);
        res.put("records",list);
        res.put("total",total);
        return res;
    }

    @Override
    public boolean addHotel(Hotel hotel) {
        return hotelMapper.insertHotel(hotel) > 0;
    }

    @Override
    public boolean updateHotel(Hotel hotel) {
        return hotelMapper.updateHotel(hotel) > 0;
    }

    @Override
    public boolean deleteHotel(Integer id) {
        return hotelMapper.deleteHotelById(id) >0;
    }

    @Override
    public Hotel getHotelById(Integer id) {
        return hotelMapper.selectHotelById(id);
    }
}