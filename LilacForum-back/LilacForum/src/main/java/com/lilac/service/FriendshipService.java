package com.lilac.service;

import com.lilac.pojo.Friendship;
import com.lilac.pojo.User;

import java.util.List;

public interface FriendshipService {

    // 发送好友请求
    void sendFriendRequest(Integer userId, Integer friendId);

    // 接受好友请求
    void acceptFriendRequest(Integer userId, Integer friendId);

    // 拒绝好友请求
    void rejectFriendRequest(Integer userId, Integer friendId);

    // 删除好友
    void removeFriend(Integer userId, Integer friendId);

    // 获取用户的好友列表
    List<User> getFriends(Integer userId);

    // 获取用户的好友请求列表
    List<Friendship> getFriendRequests(Integer userId);

    // 判断是否为好友
    boolean isFriend(Integer userId, Integer friendId);
}
