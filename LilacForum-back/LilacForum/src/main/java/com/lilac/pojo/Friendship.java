package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Friendship {
    private Integer id; // 主键ID
    private Integer userId; // 好友请求发起人ID
    private Integer friendId; // 好友请求接受人ID
    private Timestamp createdAt; // 请求的创建时间
}