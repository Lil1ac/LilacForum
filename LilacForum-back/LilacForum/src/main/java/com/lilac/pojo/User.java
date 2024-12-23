package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String gender;
    private Integer age;
    private String profession;
    private String hobby;
    private String bio;
    private String avatar;
    private String role;
}
