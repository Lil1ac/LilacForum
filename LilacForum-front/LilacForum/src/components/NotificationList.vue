<template>
    <div class="notification-list">
      <el-card v-for="notification in notifications" :key="notification.id" class="notification-item">
        <div>
          <strong>{{ notification.type }}</strong>
          <p>{{ notification.content }}</p>
          <div class="notification-footer">
            <span>{{ notification.createdAt }}</span>
            <el-button size="mini" @click="markAsRead(notification.id)" v-if="!notification.isRead">标记为已读</el-button>
            <el-button size="mini" @click="deleteNotification(notification.id)">删除</el-button>
          </div>
        </div>
      </el-card>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, ref, onMounted } from 'vue';
  import { getNotifications, markNotificationAsRead, deleteNotification } from '@/api/notification';
  import type { Notification } from '@/interface/Notification';
  import { ElMessage } from 'element-plus';
  
  export default defineComponent({
    name: 'NotificationList',
    setup() {
      const notifications = ref<Notification[]>([]);
      const userId = 1; // 假设当前用户ID为1
  
      // 获取通知列表
      const fetchNotifications = async () => {
        try {
          notifications.value = await getNotifications(userId);
        } catch (error) {
          ElMessage.error('获取通知列表失败');
        }
      };
  
      // 标记通知为已读
      const markAsRead = async (notificationId: number) => {
        try {
          await markNotificationAsRead(notificationId);
          // 更新通知状态
          const notification = notifications.value.find(n => n.id === notificationId);
          if (notification) {
            notification.isRead = true;
          }
        } catch (error) {
          ElMessage.error('标记通知为已读失败');
        }
      };
  
      // 删除通知
      const deleteNotificationHandler = async (notificationId: number) => {
        try {
          await deleteNotification(notificationId);
          // 删除通知
          notifications.value = notifications.value.filter(n => n.id !== notificationId);
        } catch (error) {
          ElMessage.error('删除通知失败');
        }
      };
  
      // 获取通知
      onMounted(() => {
        fetchNotifications();
      });
  
      return {
        notifications,
        markAsRead,
        deleteNotification: deleteNotificationHandler,
      };
    },
  });
  </script>
  
  <style scoped>
  .notification-list {
    margin-top: 20px;
  }
  
  .notification-item {
    margin-bottom: 10px;
  }
  
  .notification-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  </style>
  