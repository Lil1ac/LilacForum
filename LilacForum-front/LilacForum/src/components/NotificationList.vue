<template>
  <div class="notification-page">
    <!-- 页面标题和操作按钮 -->
    <el-card class="notification-header" shadow="always">
      <div class="header-title">通知</div>
      <el-button size="small" type="primary" @click="markAllAsRead">标记所有为已读</el-button>
    </el-card>

    <!-- 显示通知列表 -->
    <el-card v-if="notifications.length > 0" class="notification-list">
      <el-row v-for="notification in notifications" :key="notification.id" class="notification-item">
        <el-col :span="20">
          <div class="notification-title">{{ notification.type }}</div>
          <div class="notification-message">{{ notification.content }}</div>
          <div class="notification-time">{{ formatDate(notification.createdAt) }}</div>
        </el-col>
        <el-col :span="4" class="text-right">
          <el-button size="small" :type="notification.isRead ? 'success' : 'primary'"
            @click="markAsRead(notification.id)" :disabled="notification.isRead">
            {{ notification.isRead ? '已读' : '标记为已读' }}
          </el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 如果没有通知显示提示 -->
    <el-card v-else>
      <div class="no-notifications">暂无通知</div>
    </el-card>

    <!-- 分页 -->
    <el-pagination
      :current-page="currentPage"
      :page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
      layout="prev, pager, jumper, total, next, sizes"
      :page-sizes="[5, 10, 20, 30]"
    />
  </div>
</template>

<script lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getNotifications, markNotificationAsRead } from '@/api/notification';  // 引入API接口
import { useUserStore } from '@/stores/userStore';
import type { User } from '@/interface/User';
import type { Notification } from '@/interface/Notification'; // 引入通知接口

export default {
  setup() {
    // 设置默认头像
    const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

    // 定义响应式变量
    const notifications = ref<Notification[]>([]);
    const currentPage = ref(1);  // 当前页码
    const pageSize = ref(10);    // 每页显示的通知数
    const total = ref(0);        // 总通知数量

    // 获取当前用户
    const userStore = useUserStore();
    const currentUser = ref<User>({
      id: userStore.userId || 0,
      username: userStore.username,
      password: '',
      email: '',
      gender: '',
      age: 0,
      profession: '',
      hobby: '',
      bio: '',
      avatar: userStore.avatar || defaultAvatar,
      role: userStore.role,
    });

    // 获取通知列表
    const fetchNotifications = async () => {
      try {
        const response = await getNotifications(currentUser.value.id, currentPage.value, pageSize.value);  // 添加分页参数
        notifications.value = response.data;
        console.log('获取通知成功', notifications.value)
        total.value = response.total;  // 获取总数量
      } catch (error) {
        ElMessage.error('获取通知失败');
        console.error(error);
      }
    };

    // 标记单个通知为已读
    const markAsRead = async (id: number) => {
      try {
        await markNotificationAsRead(id);
        const notification = notifications.value.find(item => item.id === id);
        if (notification) {
          notification.isRead = true;
        }
      } catch (error) {
        ElMessage.error('标记通知为已读失败');
        console.error(error);
      }
    };

    // 标记所有通知为已读
    const markAllAsRead = async () => {
      try {
        for (const notification of notifications.value) {
          if (!notification.isRead) {
            await markNotificationAsRead(notification.id);
            notification.isRead = true;
          }
        }
      } catch (error) {
        ElMessage.error('标记所有通知为已读失败');
        console.error(error);
      }
    };

    // 格式化通知时间
    const formatDate = (timestamp: string) => {
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    };

    // 分页处理函数
    const handlePageChange = (page: number) => {
      currentPage.value = page;
      fetchNotifications();
    };

    // 每页显示数量变化
    const handleSizeChange = (size: number) => {
      pageSize.value = size;
      fetchNotifications();
    };

    // 在组件加载时获取通知数据
    onMounted(() => {
      fetchNotifications();
    });

    return {
      notifications,
      currentPage,
      pageSize,
      total,
      markAsRead,
      markAllAsRead,
      formatDate,
      handlePageChange,
      handleSizeChange
    };
  }
};
</script>

<style scoped>
.notification-page {
  padding: 20px;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.notification-list {
  max-height: 400px;
  overflow-y: auto;
}

.notification-item {
  margin-bottom: 20px;
  padding: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.notification-title {
  font-size: 16px;
  font-weight: bold;
}

.notification-message {
  font-size: 14px;
  color: #666;
}

.notification-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.no-notifications {
  text-align: center;
  font-size: 16px;
  color: #999;
}
</style>
