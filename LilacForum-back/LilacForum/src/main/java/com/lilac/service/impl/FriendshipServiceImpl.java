package com.lilac.service.impl;

import com.lilac.mapper.FriendshipMapper;
import com.lilac.pojo.Friendship;
import com.lilac.pojo.Notification;
import com.lilac.pojo.User;
import com.lilac.service.FriendshipService;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UserService userService; // 可能需要获取用户信息

    @Autowired
    private NotificationServiceImpl notificationService;
    // 发送好友请求
    @Override
    public void sendFriendRequest(Integer userId, Integer friendId) {
        // 检查是否已是好友
        if (isFriend(userId, friendId)) {
            throw new IllegalArgumentException("你们已经是好友");
        }

        // 1.插入好友请求记录，状态为"pending"
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus(Friendship.FriendshipStatus.PENDING);
        friendshipMapper.insertFriendRequest(friendship);

        // 2.发送通知给对方
        User user = userService.getUserById(userId);
        Notification notification = new Notification();
        notification.setUserId(friendId);
        notification.setType(Notification.NotificationType.FRIEND_REQUEST);
        notification.setContent("用户" + user.getUsername() + "请求添加你为好友");
        notificationService.createNotification(notification);
    }

    // 接受好友请求
    @Override
    public void acceptFriendRequest(Integer userId, Integer friendId) {
        Friendship friendship = friendshipMapper.getFriendship(userId, friendId);
        throw new IllegalArgumentException("没有待接受的好友请求");
    }

    // 拒绝好友请求
    @Override
    public void rejectFriendRequest(Integer userId, Integer friendId) {
        Friendship friendship = friendshipMapper.getFriendship(userId, friendId);
        if (friendship != null && "pending".equals(friendship.getStatus())) {
            friendship.setStatus(Friendship.FriendshipStatus.valueOf("rejected"));
            friendshipMapper.updateFriendshipStatus(friendship);
        } else {
            throw new IllegalArgumentException("没有待拒绝的好友请求");
        }
    }

    // 删除好友
    @Override
    public void removeFriend(Integer userId, Integer friendId) {
        friendshipMapper.deleteFriendship(userId, friendId);
    }

    // 获取用户的好友列表
    @Override
    public List<User> getFriends(Integer userId) {
        return friendshipMapper.getFriends(userId);
    }

    // 获取用户的好友请求列表
    @Override
    public List<Friendship> getFriendRequests(Integer userId) {
        return friendshipMapper.getFriendRequests(userId);
    }

    // 判断是否为好友
    @Override
    public boolean isFriend(Integer userId, Integer friendId) {
        Friendship friendship = friendshipMapper.getFriendship(userId, friendId);
        return friendship != null && "accepted".equals(friendship.getStatus());
    }
}
