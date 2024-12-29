package com.lilac.service.impl;

import com.lilac.mapper.NotificationMapper;
import com.lilac.pojo.Notification;
import com.lilac.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public void createNotification(Notification notification) {
        notificationMapper.insertNotification(notification);
    }

    @Override
    public List<Notification> getNotifications(Integer userId) {
        return notificationMapper.getNotifications(userId);
    }

    @Override
    public void markAsRead(Integer notificationId) {
        notificationMapper.updateNotificationStatus(notificationId, true);
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }
}
