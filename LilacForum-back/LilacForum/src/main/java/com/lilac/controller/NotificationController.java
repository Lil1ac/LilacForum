package com.lilac.controller;

import com.lilac.pojo.Notification;
import com.lilac.pojo.Result;
import com.lilac.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 新增通知
    @PostMapping("/addNotification")
    public Result addNotification(@RequestBody Notification notification) {
        notificationService.createNotification(notification);
        return Result.success("通知新增成功");
    }
    // 获取用户的通知列表
    @GetMapping("/getNotifications")
    public Result getNotifications(@RequestParam("userId") Integer userId) {
        List<Notification> notifications = notificationService.getNotifications(userId);
        return Result.success(notifications);
    }

    // 标记通知为已读
    @PostMapping("/markAsRead")
    public Result markAsRead(@RequestParam("notificationId") Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.success("通知已标记为已读");
    }
}
