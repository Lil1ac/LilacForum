package com.lilac.controller;

import com.lilac.pojo.Friendship;
import com.lilac.pojo.User;
import com.lilac.pojo.Result;
import com.lilac.service.FriendshipService;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    // 发送好友请求
    @GetMapping ("/sendRequest")
    public Result sendFriendRequest(@RequestParam Integer userId,
                                    @RequestParam Integer friendId) {
        try {
            friendshipService.sendFriendRequest(userId, friendId);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 接受好友请求
    @PostMapping("/acceptRequest")
    public Result acceptFriendRequest(@RequestParam("userId") Integer userId,
                                      @RequestParam("friendId") Integer friendId) {
        friendshipService.acceptFriendRequest(userId, friendId);
        return Result.success("好友请求已接受");

    }

    // 拒绝好友请求
    @PostMapping("/rejectRequest")
    public Result rejectFriendRequest(@RequestParam("userId") Integer userId,
                                      @RequestParam("friendId") Integer friendId) {
        friendshipService.rejectFriendRequest(userId, friendId);
        return Result.success("好友请求已拒绝");
    }

    // 删除好友
    @PostMapping("/removeFriend")
    public Result removeFriend(@RequestParam("userId") Integer userId,
                               @RequestParam("friendId") Integer friendId) {
        friendshipService.removeFriend(userId, friendId);
        return Result.success("好友已删除");
    }

    // 获取用户的好友列表
    @GetMapping("/getFriends")
    public Result getFriends(@RequestParam("userId") Integer userId) {
        List<User> friends = friendshipService.getFriends(userId);
        return Result.success(friends);
    }

    // 获取用户的好友请求列表
    @GetMapping("/getFriendRequests")
    public Result getFriendRequests(@RequestParam("userId") Integer userId) {
        List<Friendship> friendRequests = friendshipService.getFriendRequests(userId);
        return Result.success(friendRequests);
    }

    // 判断是否是好友
    @GetMapping("/isFriend")
    public Result isFriend(@RequestParam("userId") Integer userId,
                           @RequestParam("friendId") Integer friendId) {
        boolean isFriend = friendshipService.isFriend(userId, friendId);
        return Result.success(isFriend);
    }
}
