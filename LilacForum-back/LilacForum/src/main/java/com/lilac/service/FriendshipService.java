package com.lilac.service;

import com.lilac.pojo.PageBean;
import com.lilac.pojo.User;

import java.util.List;

public interface FriendshipService {


    List<User> getFriend(Integer id);
    // 添加好友关系
    void addFriend(Integer userId, Integer friendId);

    // 删除好友
    void removeFriend(Integer userId, Integer friendId);

    // 获取用户的好友列表
    PageBean<User> getFriendByPage(Integer userId, Integer page, Integer pageSize);


    // 判断是否为好友
    boolean isFriend(Integer userId, Integer friendId);
}
