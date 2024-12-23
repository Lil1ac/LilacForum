package com.lilac.mapper;

import com.lilac.dto.CommentRequest;
import com.lilac.pojo.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //根据帖子id获取评论
    List<CommentRequest> getCommentsByPostId(Integer postId, String sortOrder);

    //根据id获取评论
    Comment getCommentById(Integer commentId);

    //插入评论
    void insertComment(Comment comment);

    //更新评论
    void updateComment(Comment comment);

    //删除评论
    void deleteComment(Integer commentId);

    //删除贴子下的所有评论
    void deleteCommentsByPostId(Integer postId);

    List<CommentRequest> searchCommentsByCriteria(String keyword, String sortBy);
}
