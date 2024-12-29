package com.lilac.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Integer id; // 主键ID
    private Integer userId; // 接收通知的用户ID
    private NotificationType type; // 通知类型（如好友请求、消息通知等）
    private String content; // 通知内容
    private Boolean isRead = false; // 是否已读
    private Timestamp createdAt; // 通知的创建时间

    // 枚举类：定义通知类型
    public enum NotificationType {
        FRIEND_REQUEST, // 好友请求
        MESSAGE,        // 消息通知
        OTHER           // 其他类型通知
    }
}
