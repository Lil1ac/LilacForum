package com.lilac.mapper;

import com.lilac.pojo.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NotificationMapper {

    // 插入一条新的通知记录
    void insertNotification(Notification notification);

    // 获取指定用户的所有通知
    List<Notification> getNotifications(Integer userId);

    // 更新通知的状态（已读/未读）
    void updateNotificationStatus(@Param("notificationId") Integer notificationId, @Param("isRead") boolean isRead);

    // 删除指定通知
    void deleteNotification(Integer notificationId);
}
