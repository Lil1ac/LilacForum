package com.lilac.controller;

import com.lilac.dto.PostDetailRequest;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;
import com.lilac.pojo.Result;
import com.lilac.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {

    @Autowired
    private PostService postService;


    //根据ID获取帖子
    @GetMapping("/{postId}")
    public Result getPostById(@PathVariable Integer postId) {
        log.info("Get post by ID: {}", postId);
        PostDetailRequest postDetailRequest = postService.getPostById(postId);
        log.info("Post: {}", postDetailRequest);
        return Result.success(postDetailRequest);
    }


    //根据板块获取所有帖子
    @GetMapping("/sections/{sectionId}")
    public Result getPostsBySectionId(
            @PathVariable Integer sectionId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false)
            LocalDate begin,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam(required = false)
            LocalDate end,
            @RequestParam(defaultValue = "createdTime") String sortBy) {
        log.info("Get posts by section ID: {}, page: {}, pageSize: {}, sortBy: {}, begin: {}, end: {}",
                sectionId, page, pageSize, sortBy, begin, end);
        PageBean<PostDetailRequest> pageBean = postService.getPostsBySectionIdAndCriteria(
                sectionId, page, pageSize, begin, end, sortBy);
        return Result.success(pageBean);
    }

    // 根据用户ID获取所有帖子
    @GetMapping("/user/{userId}")
    public Result getPostsByUserId(
            @PathVariable Integer userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("Get posts by user ID: {}, page: {}, pageSize: {}", userId, page, pageSize);
        List<PostDetailRequest> posts = postService.getPostsByUserId(userId, page, pageSize);
        return Result.success(posts);
    }

    //发帖
    @PostMapping
    public Result createPost(@RequestBody Post post) {
        log.info("Creating post: {}", post);
        postService.insertPost(post);
        return Result.success();
    }

    //更新帖子
    @PutMapping
    public Result updatePost(@RequestBody Post post) {
        log.info("Update post: {}", post);
        postService.updatePost(post);
        return Result.success();
    }

    //删帖
    @DeleteMapping("/{postId}")
    public Result deletePost(@PathVariable Integer postId) {
        log.info("Delete post by ID: {}", postId);
        postService.deletePost(postId);
        return Result.success();
    }

    //搜索贴子
    @GetMapping("/search")
    public Result searchPost(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "title") String searchType,
            @RequestParam(defaultValue = "createdTime") String sortBy) {
        log.info("Search post: keyword:{}, page: {}, pageSize: {}, searchType: {}, sortBy: {}",
                keyword, page, pageSize, searchType, sortBy);
        PageBean<PostDetailRequest> pageBean = postService.searchPostByCriteria(keyword, page, pageSize, searchType, sortBy);
        return Result.success(pageBean);
    }
}
