package com.travel.service;

import com.travel.entity.Favorite;

import java.util.List;

public interface FavoriteService {
    List<Favorite> listByUser(Integer userId);

    boolean add(Favorite favorite);

    boolean remove(Integer userId, String targetType, Integer targetId);

    boolean removeById(Integer id);

    boolean exists(Integer userId, String targetType, Integer targetId);
}
