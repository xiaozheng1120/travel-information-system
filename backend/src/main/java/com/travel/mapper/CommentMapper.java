package com.travel.mapper;

import com.travel.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> selectByGuideId(@Param("guideId") Integer guideId);

    List<Comment> selectPage(@Param("guideId") Integer guideId,
                             @Param("start") Integer start,
                             @Param("pageSize") Integer pageSize);

    int countByGuideId(@Param("guideId") Integer guideId);

    Double avgRating(@Param("guideId") Integer guideId);

    int insertComment(Comment comment);

    int deleteById(@Param("id") Integer id);

    Comment selectById(@Param("id") Integer id);
}
