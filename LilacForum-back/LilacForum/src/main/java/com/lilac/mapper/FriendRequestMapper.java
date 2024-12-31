package com.lilac.mapper;

import com.lilac.dto.FriendReqRequest;
import com.lilac.pojo.FriendReq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface FriendRequestMapper {

    // 获取指定用户的所有待处理好友请求
    List<FriendReqRequest> getPendingRequests(Integer receiverId);

    // 获取好友请求详情
    FriendReq getRequestById(Integer id);
    FriendReq getRequestBySenderAndReceiver(Integer senderId, Integer receiverId);
    // 创建新的好友请求
    void createRequest(FriendReq friendReq);

    // 更新好友请求的状态（接受/拒绝）
    void updateRequestStatus(Integer id, String status);

    // 删除好友请求
    void deleteRequestById(Integer id);
    void deleteRequestBySenderAndReceiver(Integer senderId, Integer receiverId);
}
