package com.lilac.service;


import com.lilac.dto.FriendReqRequest;
import com.lilac.pojo.FriendReq;
import com.lilac.pojo.PageBean;

public interface FriendRequestService {

    // 获取指定用户的所有待处理好友请求
    PageBean<FriendReqRequest> getPendingRequests(
            Integer userId, Integer page, Integer pageSize);

    // 获取好友请求详情
    FriendReq getRequestById(Integer id);

    // 创建新的好友请求
    void sendFriendRequest(FriendReq friendReq);

    // 更新好友请求的状态（接受/拒绝）
    void updateRequestStatus(Integer id, String status);

    // 删除好友请求
    void deleteRequestById(Integer id);

    void deleteRequestBySenderAndReceiver(Integer senderId, Integer receiverId);

    void acceptRequestStatus(Integer friendRequestId, String status);

    void rejectRequestStatus(Integer friendRequestId, String status);
}
