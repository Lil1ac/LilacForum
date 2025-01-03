package com.lilac.service.impl;

import com.lilac.mapper.UserMapper;

import com.lilac.pojo.User;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public User getUserById(Integer id) {return userMapper.getUserById(id);}
    @Override
    public void insertUser(User user) {
        userMapper.insertUser(user);
    }
    @Override
    public void updateUser(User user) {
        userMapper.updateUser(user);
    }
    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }

    @Override
    public User getUserByUsername(String username) {
        return userMapper.getUserByUsername(username);
    }
}
