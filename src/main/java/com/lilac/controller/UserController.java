package com.lilac.controller;


import com.aliyuncs.ram.model.v20150501.UpdateUserRequest;
import com.lilac.pojo.Result;
import com.lilac.pojo.User;
import com.lilac.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
}
