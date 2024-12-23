package com.lilac.mapper;

import com.lilac.dto.PostDetailRequest;
import com.lilac.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PostMapper {
    //根据条件获取帖子分页
    List<PostDetailRequest> getPostsBySectionIdAndCriteria(Integer sectionId,
                                                           String sortBy,
                                                           LocalDate begin,
                                                           LocalDate end);

    //根据sectionId获取帖子
    List<Post> getPostsBySectionId(Integer sectionId);

    //根据userId帖子
    List<PostDetailRequest> getPostsByUserId(Integer userId);

    // 插入新帖子
    void insertPost(Post post);

    // 获取指定帖子的详细信息
    PostDetailRequest getPostById(Integer postId);


    // 更新帖子
    void updatePost(Post post);

    void updatePostLastReplyTime(Integer postId,LocalDateTime lastReplyTime);

    // 删除帖子
    void deletePost(Integer postId);

    // 根据sectionId删除帖子
    void deletePostsBySectionId(Integer sectionId);

    // 根据关键字搜索帖子
    List<PostDetailRequest> searchPostByCriteria(String keyword,String searchType, String sortBy);
}
