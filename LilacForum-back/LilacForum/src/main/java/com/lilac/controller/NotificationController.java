package com.lilac.controller;

import com.lilac.pojo.Notification;
import com.lilac.pojo.PageBean;
import com.lilac.pojo.Result;
import com.lilac.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    @GetMapping("/getNotifications/{userId}")
    public Result getNotifications(@PathVariable Integer userId,
                                   @RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "10") Integer pageSize,
                                   @RequestParam(defaultValue = "createdTime") String sortBy) {
        PageBean<Notification> notifications = notificationService.getNotifications(userId, page, pageSize, sortBy);
        return Result.success(notifications);
    }

    // 标记通知为已读
    @PutMapping("/{notificationId}/markAsRead")
    public Result markAsRead(@PathVariable Integer notificationId) {
        notificationService.markAsRead(notificationId);
        return Result.success("通知已标记为已读");
    }


    //获取通知数量
    @GetMapping("/count")
    public Result getNotificationCount(@RequestParam("userId") Integer userId) {
        Integer count =  notificationService.getNotificationCount(userId);
        return Result.success(count);
    }

}
