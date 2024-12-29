// src/api/notification.ts
import request from './request'; // 假设 request 已封装好
import type { Notification } from '@/interface/Notification';
import { ElMessage } from 'element-plus';

// 获取用户的通知列表
export const getNotifications = async (userId: number): Promise<Notification[]> => {
  try {
    const response = await request.get(`/notifications/${userId}`);
    const result = response.data;
    if (result.code === 1) {
      return result.data.notifications;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取通知失败，请重试');
    throw error;
  }
};

// 标记通知为已读
export const markNotificationAsRead = async (notificationId: number): Promise<boolean> => {
  try {
    const response = await request.put(`/notifications/${notificationId}/read`);
    const result = response.data;
    if (result.code === 1) {
      return true; // 标记成功
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    throw error;
  }
};

// 删除通知
export const deleteNotification = async (notificationId: number): Promise<boolean> => {
  try {
    const response = await request.delete(`/notifications/${notificationId}`);
    const result = response.data;
    if (result.code === 1) {
      return true; // 删除成功
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    throw error;
  }
};
