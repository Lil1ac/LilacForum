package com.lilac.service;

import com.lilac.pojo.User;

public interface UserService {
    User getUserById(Integer id);
    void insertUser(User user);
    void updateUser(User user);
    void deleteUser(Integer id);
}