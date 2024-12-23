package com.lilac.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.dto.PostDetailRequest;
import com.lilac.mapper.CommentMapper;
import com.lilac.mapper.PostMapper;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;
import com.lilac.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public PageBean<PostDetailRequest> getPostsBySectionIdAndCriteria
            (Integer sectionId, Integer page, Integer pageSize, LocalDate begin, LocalDate end, String sortBy) {
        PageHelper.startPage(page, pageSize);
        List<PostDetailRequest> posts = postMapper.getPostsBySectionIdAndCriteria(sectionId, sortBy, begin, end);
        PageInfo<PostDetailRequest> pageInfo = new PageInfo<>(posts);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public List<PostDetailRequest> getPostsByUserId(Integer userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        return postMapper.getPostsByUserId(userId);
    }

    @Override
    public PostDetailRequest getPostById(Integer postId) {
        return postMapper.getPostById(postId);
    }

    @Override
    public void insertPost(Post post) {
        postMapper.insertPost(post);
    }

    @Override
    public void updatePost(Post post) {
        postMapper.updatePost(post);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePost(Integer postId) {
        commentMapper.deleteCommentsByPostId(postId);
        postMapper.deletePost(postId);
    }

    @Override
    public PageBean<PostDetailRequest> searchPostByCriteria(
            String keyword, Integer page, Integer pageSize, String searchType, String sortBy) {
        PageHelper.startPage(page, pageSize);
        List<PostDetailRequest> posts = postMapper.searchPostByCriteria(keyword, searchType, sortBy);
        log.info("searchPostByCriteria: posts: " + posts);
        PageInfo<PostDetailRequest> pageInfo = new PageInfo<>(posts);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
