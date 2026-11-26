package com.travel.mapper;

import com.travel.entity.Favorite;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    int insertFavorite(Favorite favorite);

    int deleteFavorite(@Param("userId") Integer userId,
                       @Param("targetType") String targetType,
                       @Param("targetId") Integer targetId);

    int deleteById(@Param("id") Integer id);

    List<Favorite> selectByUser(@Param("userId") Integer userId);

    int checkExists(@Param("userId") Integer userId,
                    @Param("targetType") String targetType,
                    @Param("targetId") Integer targetId);
}
