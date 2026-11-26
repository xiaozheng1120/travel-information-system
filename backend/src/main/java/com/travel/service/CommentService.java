package com.travel.service;

import com.travel.entity.Comment;
import com.travel.entity.Result;

import java.util.List;
import java.util.Map;

public interface CommentService {
    List<Comment> listByGuide(Integer guideId);

    Map<String, Object> stats(Integer guideId);

    Result addComment(Comment comment);

    Result deleteComment(Integer id, Integer userId);
}
