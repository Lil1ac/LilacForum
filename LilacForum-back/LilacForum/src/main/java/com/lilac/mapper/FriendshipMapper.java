package com.lilac.mapper;

import com.lilac.pojo.Friendship;
import com.lilac.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FriendshipMapper {

    // 插入一条好友请求记录
    void insertFriendRequest(Friendship friendship);

    // 更新好友请求状态
    void updateFriendshipStatus(Friendship friendship);

    // 删除好友关系
    void deleteFriendship(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

    // 获取用户的所有好友
    List<User> getFriends(Integer userId);

    // 获取用户的所有好友请求
    List<Friendship> getFriendRequests(Integer userId);

    // 获取两人之间的好友关系（如果有的话）
    Friendship getFriendship(@Param("userId") Integer userId, @Param("friendId") Integer friendId);
}
