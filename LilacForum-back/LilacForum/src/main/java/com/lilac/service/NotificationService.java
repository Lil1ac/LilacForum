package com.lilac.service;

import com.lilac.pojo.Notification;
import com.lilac.pojo.PageBean;

import java.util.List;

public interface NotificationService {

    /**
     * 创建新的通知
     * @param notification 通知对象
     */
    void createNotification(Notification notification);

    /**
     * 获取用户的所有通知
     * @param userId 用户ID
     * @return 通知列表
     */
    PageBean<Notification> getNotifications(
            Integer userId, Integer page, Integer pageSize, String sortBy);

    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     */
    void markAsRead(Integer notificationId);

    /**
     * 删除通知
     * @param notificationId 通知ID
     */
    void deleteNotification(Integer notificationId);

    Integer getNotificationCount(Integer userId);
}
