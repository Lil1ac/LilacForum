package com.lilac.controller;

import com.lilac.pojo.Result;
import com.lilac.pojo.User;
import com.lilac.service.AuthService;
import com.lilac.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public Result register(@RequestBody User user) {
        //检查用户名是否已存在
        boolean userExists = authService.userExists(user.getUsername());
        if (userExists) {
            return Result.error("用户名已存在");
        }
        //注册
        try {
            authService.register(user);
            return Result.success("注册成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败");
        }
    }

    @PostMapping("/login")
    public Result login(@RequestBody User user) {
        User loggedInUser = authService.login(user);
        if(loggedInUser != null){
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", loggedInUser.getId());
            claims.put("username", loggedInUser.getUsername());
            String jwt = JwtUtils.generateJwt(claims);
            return Result.success(jwt);
        }
        return Result.error("用户名或密码错误");
    }
}
