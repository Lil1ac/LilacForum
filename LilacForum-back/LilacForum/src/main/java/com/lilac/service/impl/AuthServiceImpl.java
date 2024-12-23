package com.lilac.service.impl;

import com.lilac.mapper.UserMapper;
import com.lilac.pojo.User;
import com.lilac.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;
    @Override
    public void register(User user) {
        userMapper.insertUser(user);
    }
    @Override
    public User login(User user) {
        return userMapper.getUserByUsernameAndPassword(user);
    }
    @Override
    public boolean userExists(String username) {
        return userMapper.getUserByUsername(username) != null;
    }

}
