package com.travel.controller;

import com.travel.entity.Comment;
import com.travel.entity.Result;
import com.travel.service.CommentService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    // 某篇攻略的全部评论
    @GetMapping("/list")
    public List<Comment> list(@RequestParam Integer guideId) {
        return commentService.listByGuide(guideId);
    }

    // 评论数量 + 平均评分
    @GetMapping("/stats")
    public Map<String, Object> stats(@RequestParam Integer guideId) {
        return commentService.stats(guideId);
    }

    // 发布评论
    @PostMapping("/add")
    public Result add(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    // 删除自己的评论
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, @RequestParam(required = false) Integer userId) {
        return commentService.deleteComment(id, userId);
    }
}
