package com.lilac.controller;

import com.lilac.dto.FriendReqRequest;
import com.lilac.pojo.FriendReq;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Result;
import com.lilac.service.FriendRequestService;
import com.lilac.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendRequest")
public class FriendRequestController {

    @Autowired
    private FriendRequestService friendRequestService;


    // 获取用户的所有待处理好友请求
    @GetMapping("/getPendingRequests/{userId}")
    public Result getPendingRequests(@PathVariable Integer userId,
                                     @RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean<FriendReqRequest> pendingRequests = friendRequestService.getPendingRequests(userId, page, pageSize);
        return Result.success(pendingRequests);
    }

    // 创建新的好友请求
    @PostMapping("/sendRequest")
    public Result createRequest(@RequestBody FriendReq friendReq) {
        try {
            friendRequestService.sendFriendRequest(friendReq);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        return Result.success();
    }


    // 接受好友请求
    @GetMapping("/acceptRequest")
    public Result acceptRequestStatus(@RequestParam Integer friendRequestId, @RequestParam String status) {
        friendRequestService.acceptRequestStatus(friendRequestId, status);
        return Result.success();
    }
    // 拒绝好友请求
    @GetMapping("/rejectRequest")
    public Result rejectRequestStatus(@RequestParam Integer friendRequestId, @RequestParam String status) {
        friendRequestService.rejectRequestStatus(friendRequestId, status);
        return Result.success();
    }

    // 删除好友请求
    @DeleteMapping("/deleteRequest")
    public Result deleteRequest(@RequestParam("id") Integer id) {
        friendRequestService.deleteRequestById(id);
        return Result.success();
    }
}
