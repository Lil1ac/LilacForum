package com.lilac.service;

import com.lilac.dto.CommentRequest;
import com.lilac.pojo.Comment;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;

import java.time.LocalDate;
import java.util.List;

public interface CommentService {

    PageBean<CommentRequest> getCommentsByPostIdAndCriteria(
            Integer postId, Integer page, Integer pageSize, String sortBy);

    Comment getCommentById(Integer commentId);

    void insertComment(Comment comment);

    void updateComment(Comment comment);

    void deleteComment(Integer commentId);

    PageBean<CommentRequest> searchCommentsByCriteria(String keyword, Integer page, Integer pageSize, String sortBy);
}
