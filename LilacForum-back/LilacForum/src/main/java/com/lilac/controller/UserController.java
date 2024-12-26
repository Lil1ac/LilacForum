package com.lilac.controller;


import com.aliyuncs.ram.model.v20150501.UpdateUserRequest;
import com.lilac.pojo.Result;
import com.lilac.pojo.User;
import com.lilac.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/user/info")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    public Result getUserInfo(@PathVariable Integer userId) {
        log.info("userId: {}", userId);
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    @PutMapping
    public Result updateUserInfo(@RequestBody User user) {
        log.info("user: {}", user);
        userService.updateUser(user);
        return Result.success();
    }

    // 根据角色获取用户列表
    @GetMapping("/role")
    public Result getUsersByRole(@RequestParam String role, @RequestParam String fromUser) {
        log.info("Getting users with role: {} excluding from user: {}", role, fromUser);

        // 获取所有用户列表
        List<User> users = userService.getAllUsers();

        // 过滤掉当前用户以及角色不匹配的用户
        List<User> filteredUsers = users.stream()
                .filter(user -> !user.getUsername().equals(fromUser) && user.getRole().equals(role))
                .collect(Collectors.toList());

        return Result.success(filteredUsers);
    }
}
