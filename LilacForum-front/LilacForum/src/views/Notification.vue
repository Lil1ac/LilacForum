<template>
    <div class="notification-page">
        <!-- 页面标题和操作按钮 -->
        <el-card class="notification-header">
            <div class="header-title">通知</div>
            <el-button size="small" type="primary" @click="markAllAsRead" class="mark-all-button">
                标记所有为已读
            </el-button>
        </el-card>

        <!-- 显示通知列表 -->
        <el-card v-if="notifications && notifications.length > 0" class="notification-list">
            <el-row v-for="notification in notifications" :key="notification.id" class="notification-item">
                <el-col :span="20">
                    <!-- 根据 notification.type 显示不同的标题 -->
                    <div class="notification-title">
                        {{ getNotificationTitle(notification.type) }}
                    </div>
                    <div class="notification-message">{{ notification.content }}</div>
                    <div class="notification-time">{{ format.formatDate(notification.createdAt) }}</div>
                </el-col>
                <el-col :span="4" class="text-right">
                    <el-button size="small" :type="notification.isRead ? 'success' : 'primary'"
                        @click="markAsRead(notification.id)" :disabled="notification.isRead" class="read-button">
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
        <el-pagination :current-page="currentPage" :page-size="pageSize" :total="total"
            @current-change="handlePageChange" @size-change="handleSizeChange"
            layout="prev, pager, jumper, total, next, sizes" :page-sizes="[5, 10, 20, 30]" />
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getNotificationCount, getNotifications, markNotificationAsRead } from '@/api/notification';
import { useUserStore } from '@/stores/userStore';
import type { User } from '@/interface/User';
import type { Notification } from '@/interface/Notification';
import Pagination from '@/components/Pagination.vue';
import format from '@/utils/format';

const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';

const notifications = ref<Notification[]>([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

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
        const notificationList = await getNotifications(currentUser.value.id, currentPage.value, pageSize.value);
        notifications.value = notificationList.data;
        total.value = notificationList.total;
    } catch (error) {
        ElMessage.error('获取通知失败');
        console.error(error);
    }
};

// 标记单个通知为已读
const markAsRead = async (id: number) => {
    try {
        await markNotificationAsRead(id);
        await getNotificationCount(currentUser.value.id); 
        const notification = notifications.value.find((item) => item.id === id);
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
    for (const notification of notifications.value) {
        if (!notification.isRead) {
            await markNotificationAsRead(notification.id);
            notification.isRead = true;
        }
    }
    await getNotificationCount(currentUser.value.id); 
};

// 处理分页变更
const handlePageChange = (page: number) => {
    currentPage.value = page;
    fetchNotifications();
};

// 处理每页显示数量变化
const handleSizeChange = (size: number) => {
    pageSize.value = size;
    fetchNotifications();
};

// 初始化加载通知数据
onMounted(() => {
    fetchNotifications();
});

// 获取通知类型的显示内容
const getNotificationTitle = (type: string) => {
    switch (type) {
        case 'friend_request':
            return '好友申请通知';
        case 'comment':
            return '评论通知';
        default:
            return '通知';
    }
};
</script>


<style scoped>
.notification-page {
    max-width: 900px;
    margin: 0 auto;
    padding: 30px;
    background-color: #f6f6f6;
}

.notification-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    background-color: #ffffff;
    padding: 20px;
}

.header-title {
    font-size: 22px;
    font-weight: 600;
    color: #333;
    margin: 0;
}

.mark-all-button {
    background-color: #007aff;
    color: white;
    font-weight: 500;
    padding: 8px 16px;
    transition: background-color 0.3s ease;
}

.mark-all-button:hover {
    background-color: #005bb5;
}

.notification-list {
    margin-top: 20px;
}

.notification-item {
    padding: 15px;
    background-color: #ffffff;
    margin-bottom: 10px;
    border-bottom: 1px solid #f0f0f0;
}

.notification-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
}

.notification-message {
    font-size: 14px;
    color: #555;
    margin-bottom: 8px;
}

.notification-time {
    font-size: 12px;
    color: #888;
}

.no-notifications {
    text-align: center;
    font-size: 16px;
    color: #888;
}

.el-pagination {
    margin-top: 20px;
    text-align: center;
}

.read-button {
    border-radius: 20px;
    font-weight: 500;
    padding: 6px 12px;
}

.read-button:disabled {
    background-color: #e1e1e1;
    color: #ccc;
}
</style>
