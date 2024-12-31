
import request from './request'; 
import type { Notification } from '@/interface/Notification';
import { ElMessage } from 'element-plus';
import type { PageBean } from '@/interface/PageBean';

// 获取用户的通知列表（支持分页）
export const getNotifications = async (
  userId: number,
  page: number,
  pageSize: number,
  sortBy: string = 'desc'): Promise<PageBean<Notification>> => {
  try {
    const response = await request.get(`/notification/getNotifications/${userId}`, {
      params: {
        page,
        pageSize,
        sortBy
      }
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data;  // 返回通知列表和总数
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
    const response = await request.put(`/notification/${notificationId}/markAsRead`);
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
    const response = await request.delete(`/notification/${notificationId}`);
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

//获取通知数量
export const getNotificationCount = async (userId: number): Promise<number> => {
  try {
    const response = await request.get(`/notification/count`, {
      params: { userId }
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data.count;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    throw error;
  }
}
