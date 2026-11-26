package com.travel.service;

import com.travel.entity.Hotel;
import java.util.Map;

public interface HotelService {
    Map<String,Object> getHotelPage(Integer pageNum, Integer pageSize, Hotel hotel);
    boolean addHotel(Hotel hotel);
    boolean updateHotel(Hotel hotel);
    boolean deleteHotel(Integer id);
    Hotel getHotelById(Integer id);
}