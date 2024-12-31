package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.dto.FriendReqRequest;
import com.lilac.mapper.FriendRequestMapper;
import com.lilac.pojo.*;
import com.lilac.service.FriendRequestService;
import com.lilac.service.FriendshipService;
import com.lilac.service.NotificationService;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {

    @Autowired
    private FriendRequestMapper friendRequestMapper;

    @Lazy
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private NotificationService notificationService;

    // 获取指定用户的所有待处理好友请求
    @Override
    public PageBean<FriendReqRequest> getPendingRequests(Integer userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<FriendReqRequest> friendReqs = friendRequestMapper.getPendingRequests(userId);
        PageInfo<FriendReqRequest> pageInfo = new PageInfo<>(friendReqs);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }

    // 获取好友请求详情
    @Override
    public FriendReq getRequestById(Integer id) {
        return friendRequestMapper.getRequestById(id);
    }

    // 发送好友请求
    @Override
    public void sendFriendRequest(FriendReq friendReq) {
        // 检查是否已经是好友
        if (friendshipService.isFriend(friendReq.getReceiverId(), friendReq.getSenderId())) {
            throw new IllegalArgumentException("你们已经是好友");
        }
        // 检查自己是否已经发送过好友请求
        FriendReq sentRequest = friendRequestMapper.getRequestBySenderAndReceiver(friendReq.getSenderId(), friendReq.getReceiverId());
        if (sentRequest != null) {
            switch (sentRequest.getStatus()) {
                case "pending" ->  //如果还未被接受，则不能再发送
                        throw new IllegalArgumentException("你已经发送过好友请求");
                case "rejected" ->  //如果被拒绝，则不能再发送
                        throw new IllegalArgumentException("对方已拒绝你的好友请求");
                case "accepted" -> //如果已经是好友，则不能再发送
                        throw new IllegalArgumentException("你们已经是好友");
            }
        }

        //检查对方是否发送过好友请求，如果对方也发送过好友请求，则自动接受
        if (friendRequestMapper.getRequestBySenderAndReceiver(
                friendReq.getReceiverId(), friendReq.getSenderId()) != null) {
            updateRequestStatus(friendReq.getId(), "accepted");
            friendshipService.addFriend(friendReq.getSenderId(), friendReq.getReceiverId());
            return;
        }

        // 插入新的好友请求记录，状态为 "pending"
        friendReq.setStatus("pending");
        friendRequestMapper.createRequest(friendReq);

        // 发送通知给对方
        Notification notification = new Notification();
        notification.setUserId(friendReq.getReceiverId());
        notification.setType("friend_request");
        if (friendReq.getContent().isEmpty()) {
            notification.setContent("用户" + friendReq.getSenderId() + "请求添加你为好友");
        } else {
            notification.setContent(friendReq.getContent());
        }
        notificationService.createNotification(notification);
    }

    // 更新好友请求的状态（接受/拒绝）
    @Override
    public void updateRequestStatus(Integer id, String status) {
        friendRequestMapper.updateRequestStatus(id, status);
    }

    // 接受好友请求
    @Override
    public void acceptRequestStatus(Integer friendRequestId, String status) {
        updateRequestStatus(friendRequestId, "accepted");
        //根据请求id获取双方的id
        FriendReq friendReq = getRequestById(friendRequestId);
        //添加好友关系
        friendshipService.addFriend(friendReq.getSenderId(), friendReq.getReceiverId());
    }

    // 拒绝好友请求
    @Override
    public void rejectRequestStatus(Integer friendRequestId, String status) {
        updateRequestStatus(friendRequestId, "rejected");
    }
    // 删除好友请求
    @Override
    public void deleteRequestById(Integer id) {
        friendRequestMapper.deleteRequestById(id);
    }
    @Override
    public void deleteRequestBySenderAndReceiver(Integer senderId, Integer receiverId) {
        friendRequestMapper.deleteRequestBySenderAndReceiver(senderId, receiverId);
    }
}
