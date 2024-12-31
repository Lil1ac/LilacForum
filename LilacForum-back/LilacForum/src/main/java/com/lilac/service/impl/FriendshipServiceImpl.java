package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.mapper.FriendshipMapper;
import com.lilac.pojo.Friendship;
import com.lilac.pojo.Notification;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.User;
import com.lilac.service.FriendRequestService;
import com.lilac.service.FriendshipService;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private FriendRequestService friendRequestService;

    @Autowired
    private NotificationServiceImpl notificationService;


    @Override
    public void addFriend(Integer userId, Integer friendId) {
        Friendship friendship = new Friendship(null, userId, friendId, null);
        friendshipMapper.insertFriendRequest(friendship);
    }

    // 删除好友
    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        // 删除好友请求
        friendRequestService.deleteRequestBySenderAndReceiver(userId, friendId);
        // 删除好友关系
        friendshipMapper.deleteFriendship(userId, friendId);

    }

    //获取好友列表
    @Override
    public List<User> getFriend(Integer userId) {
        return friendshipMapper.getFriends(userId);
    }

    // 获取用户的好友列表分页
    @Override
    public PageBean<User> getFriendByPage(Integer userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<User> friendList = friendshipMapper.getFriends(userId);
        PageInfo<User> pageInfo = new PageInfo<>(friendList);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());

    }

    // 判断是否为好友
    @Override
    public boolean isFriend(Integer userId, Integer friendId) {
        Friendship friendship = friendshipMapper.getFriendship(userId, friendId);
        return friendship != null;
    }
}
