package com.travel.service.impl;

import com.travel.entity.Comment;
import com.travel.entity.Result;
import com.travel.entity.User;
import com.travel.mapper.CommentMapper;
import com.travel.mapper.UserMapper;
import com.travel.service.CommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserMapper userMapper;

    @Override
    public List<Comment> listByGuide(Integer guideId) {
        return commentMapper.selectByGuideId(guideId);
    }

    @Override
    public Map<String, Object> stats(Integer guideId) {
        Map<String, Object> map = new HashMap<>();
        int count = commentMapper.countByGuideId(guideId);
        Double avg = commentMapper.avgRating(guideId);
        map.put("count", count);
        map.put("avgRating", avg == null ? 0 : Math.round(avg * 10) / 10.0);
        return map;
    }

    @Override
    public Result addComment(Comment comment) {
        if (comment == null || comment.getGuideId() == null) {
            return Result.error("攻略不存在");
        }
        if (comment.getUserId() == null) {
            return Result.error("请先登录再评论");
        }
        String content = comment.getContent() == null ? "" : comment.getContent().trim();
        if (content.isEmpty()) {
            return Result.error("评论内容不能为空");
        }
        if (content.length() > 500) {
            return Result.error("评论最多 500 字");
        }
        User user = userMapper.findUserById(comment.getUserId());
        if (user == null) {
            return Result.error("用户不存在，请重新登录");
        }
        comment.setContent(content);
        comment.setUsername(user.getName() == null || user.getName().isEmpty() ? user.getUsername() : user.getName());
        comment.setAvatar(user.getAvatar());
        if (comment.getRating() != null) {
            double rating = comment.getRating();
            if (rating < 0 || rating > 5) {
                return Result.error("评分需在 0~5 之间");
            }
        }
        int affect = commentMapper.insertComment(comment);
        return affect > 0 ? Result.success("评论发布成功") : Result.error("评论失败，请重试");
    }

    @Override
    public Result deleteComment(Integer id, Integer userId) {
        if (id == null) {
            return Result.error("评论不存在");
        }
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.error("评论不存在或已删除");
        }
        if (userId == null || !userId.equals(comment.getUserId())) {
            return Result.error("只能删除自己的评论");
        }
        int affect = commentMapper.deleteById(id);
        return affect > 0 ? Result.success("评论已删除") : Result.error("删除失败");
    }
}
