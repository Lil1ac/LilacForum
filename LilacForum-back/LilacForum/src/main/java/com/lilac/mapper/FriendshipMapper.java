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

    // 删除好友关系
    void deleteFriendship(Integer userId, Integer friendId);

    // 获取用户的所有好友
    List<User> getFriends(Integer userId);

    // 获取两人之间的好友关系（如果有的话）
    Friendship getFriendship(Integer userId, Integer friendId);
}
