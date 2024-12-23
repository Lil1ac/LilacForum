package com.lilac.service;


import com.lilac.dto.PostDetailRequest;
import com.lilac.pojo.Comment;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Post;

import java.time.LocalDate;
import java.util.List;

public interface PostService {

    PageBean<PostDetailRequest> getPostsBySectionIdAndCriteria(
            Integer sectionId, Integer page,
            Integer pageSize, LocalDate begin,
            LocalDate end, String sortBy);
    List<PostDetailRequest> getPostsByUserId(Integer userId, Integer page, Integer pageSize);


    PostDetailRequest getPostById(Integer postId);

    void insertPost(Post post);

    void updatePost(Post post);

    void deletePost(Integer postId);

    PageBean<PostDetailRequest> searchPostByCriteria(
            String keyword, Integer page, Integer pageSize, String searchType, String sortBy);
}
