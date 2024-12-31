package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendReq {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String status;  // 请求状态（PENDING, ACCEPTED, REJECTED）
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String content;
}
