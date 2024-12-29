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
    private FriendshipStatus status; // 好友请求的状态（PENDING, ACCEPTED, REJECTED）
    private Timestamp createdAt; // 请求的创建时间
    private Timestamp updatedAt; // 请求的更新时间
    // 枚举类：定义好友请求的状态
    public enum FriendshipStatus {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}