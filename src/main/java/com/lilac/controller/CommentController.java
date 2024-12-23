package com.lilac.controller;

import com.lilac.dto.CommentRequest;
import com.lilac.pojo.Comment;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;
import com.lilac.pojo.Result;
import com.lilac.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 获取指定帖子的评论
    @GetMapping("/posts/{postId}")
    public Result getCommentsByPostId(@PathVariable Integer postId,
                                      @RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize,
                                      @RequestParam(defaultValue = "asc") String sortBy) {
        log.info("getCommentsByPostId: postId = {}, page = {}, pageSize = {}, sortBy = {}", postId, page, pageSize, sortBy);
        PageBean<CommentRequest> pageBean = commentService.getCommentsByPostIdAndCriteria(
                postId, page, pageSize, sortBy);
        return Result.success(pageBean);
    }

    //发评论
    @PostMapping
    public Result createPost(@RequestBody Comment comment) {
        log.info("createComment: comment = {}", comment);
        commentService.insertComment(comment);
        return Result.success();
    }

    //删除评论
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable Integer commentId) {
        log.info("deleteComment: commentId = {}", commentId);
        commentService.deleteComment(commentId);
        return Result.success();
    }

    //搜索回复
    @GetMapping("/search")
    public Result searchComment(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "createdTime") String sortBy) {
        log.info("searchComment: keyword = {}, page = {}, pageSize = {}, sortBy = {}", keyword, page, pageSize, sortBy);
        PageBean<CommentRequest> pageBean = commentService.searchCommentsByCriteria(keyword, page, pageSize, sortBy);
        return Result.success(pageBean);
    }
}
