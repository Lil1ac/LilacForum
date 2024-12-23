package com.lilac.mapper;

import com.lilac.pojo.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    User getUserByUsernameAndPassword(User user);

    User getUserByUsername(String username);

    User getUserById(int id);

    void insertUser(User user);

    void updateUser(User user);

    void deleteUser(int id);
}
