package com.lilac.service;

import com.lilac.pojo.User;

public interface AuthService {
    void register(User user);
    User login(User user);
    boolean userExists(String username);

}
