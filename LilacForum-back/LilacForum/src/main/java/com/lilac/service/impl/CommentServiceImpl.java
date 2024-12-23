package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.dto.CommentRequest;
import com.lilac.mapper.CommentMapper;
import com.lilac.mapper.PostMapper;
import com.lilac.pojo.Comment;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;
import com.lilac.service.CommentService;
import com.lilac.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public PageBean<CommentRequest> getCommentsByPostIdAndCriteria(
            Integer postId, Integer page, Integer pageSize, String sortBy) {
        PageHelper.startPage(page, pageSize);
        List<CommentRequest> comments = commentMapper.getCommentsByPostId(postId, sortBy);
        PageInfo<CommentRequest> pageInfo = new PageInfo<>(comments);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    @Override
    public Comment getCommentById(Integer commentId) {
        return commentMapper.getCommentById(commentId);
    }

    @Override
    public void insertComment(Comment comment) {
        postMapper.updatePostLastReplyTime(comment.getPostId(), LocalDateTime.now());
        commentMapper.insertComment(comment);
    }

    @Override
    public void updateComment(Comment comment) {
        commentMapper.updateComment(comment);
    }

    @Override
    public void deleteComment(Integer commentId) {
        commentMapper.deleteComment(commentId);
    }

    @Override
    public PageBean<CommentRequest> searchCommentsByCriteria(String keyword, Integer page, Integer pageSize, String sortBy) {
        PageHelper.startPage(page, pageSize);
        List<CommentRequest> comments = commentMapper.searchCommentsByCriteria(keyword, sortBy);
        PageInfo<CommentRequest> pageInfo = new PageInfo<>(comments);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }
}
