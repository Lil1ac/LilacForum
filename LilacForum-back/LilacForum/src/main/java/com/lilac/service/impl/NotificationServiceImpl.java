package com.lilac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lilac.dto.PostDetailRequest;
import com.lilac.mapper.NotificationMapper;
import com.lilac.pojo.Notification;
import com.lilac.pojo.PageBean;
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
    public PageBean<Notification> getNotifications(
            Integer userId, Integer page, Integer pageSize, String sortBy) {
        PageHelper.startPage(page, pageSize);
        List<Notification> notifications = notificationMapper.getNotifications(userId, sortBy);
        PageInfo<Notification> pageInfo = new PageInfo<>(notifications);
        return new PageBean<>(pageInfo.getList(), pageInfo.getTotal(), pageInfo.getPageNum(), pageInfo.getPageSize());
    }


    @Override
    public void markAsRead(Integer notificationId) {
        notificationMapper.updateNotificationStatus(notificationId, true);
    }

    @Override
    public void deleteNotification(Integer notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }

    @Override
    public Integer getNotificationCount(Integer userId) {
        return notificationMapper.getNotificationCount(userId);
    }
}
