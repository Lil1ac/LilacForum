package com.lilac.controller;

import com.lilac.pojo.PageBean;
import com.lilac.pojo.Result;
import com.lilac.pojo.User;
import com.lilac.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;


    // 获取用户的好友列表（不分页）
    @GetMapping("/getFriends/{userId}")
    public Result getFriends(@PathVariable Integer userId) {
        return Result.success(friendshipService.getFriend(userId));
    }

    // 获取用户的好友列表（分页）
    @GetMapping("/getFriendsPage/{userId}")
    public Result getFriends(@PathVariable Integer userId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageBean<User> friends = friendshipService.getFriendByPage(userId, page, pageSize);
        return Result.success(friends);
    }

    // 判断是否是好友
    @GetMapping("/isFriend")
    public Result isFriend(@RequestParam("userId") Integer userId,
                           @RequestParam("friendId") Integer friendId) {
        boolean isFriend = friendshipService.isFriend(userId, friendId);
        return Result.success(isFriend);
    }


    // 删除好友
    @DeleteMapping("/removeFriend")
    public Result removeFriend(@RequestParam("userId") Integer userId,
                               @RequestParam("friendId") Integer friendId) {
        friendshipService.removeFriend(userId, friendId);
        return Result.success();
    }
}
