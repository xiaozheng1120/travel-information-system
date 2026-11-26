package com.travel.service.impl;

import com.travel.entity.Favorite;
import com.travel.mapper.FavoriteMapper;
import com.travel.service.FavoriteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Resource
    private FavoriteMapper favoriteMapper;

    @Override
    public List<Favorite> listByUser(Integer userId) {
        if (userId == null) {
            return java.util.Collections.emptyList();
        }
        return favoriteMapper.selectByUser(userId);
    }

    @Override
    public boolean add(Favorite favorite) {
        if (favorite == null || favorite.getUserId() == null || favorite.getTargetId() == null
                || favorite.getTargetType() == null || favorite.getTargetType().isEmpty()) {
            return false;
        }
        // 已经收藏过就不再重复插入
        if (favoriteMapper.checkExists(favorite.getUserId(), favorite.getTargetType(), favorite.getTargetId()) > 0) {
            return true;
        }
        return favoriteMapper.insertFavorite(favorite) > 0;
    }

    @Override
    public boolean remove(Integer userId, String targetType, Integer targetId) {
        if (userId == null || targetType == null || targetId == null) {
            return false;
        }
        return favoriteMapper.deleteFavorite(userId, targetType, targetId) > 0;
    }

    @Override
    public boolean removeById(Integer id) {
        return id != null && favoriteMapper.deleteById(id) > 0;
    }

    @Override
    public boolean exists(Integer userId, String targetType, Integer targetId) {
        if (userId == null || targetType == null || targetId == null) {
            return false;
        }
        return favoriteMapper.checkExists(userId, targetType, targetId) > 0;
    }
}
