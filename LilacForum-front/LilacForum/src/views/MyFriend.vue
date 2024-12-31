<template>
  <div class="friendship-page">
    <!-- 添加好友按钮 -->
    <el-button @click="openAddFriendDialog" type="primary" class="add-friend-btn">添加好友</el-button>

    <!-- 好友请求列表 -->
    <el-card v-if="pendingRequests.length > 0" class="card-container">
      <div class="card-header">好友请求</div>
      <div class="user-list">
        <div v-for="request in pendingRequests" :key="request.id" class="user-item">
          <div class="user-left">
            <img :src="request.sender.avatar || defaultAvatar" alt="头像" class="user-avatar" />
          </div>
          <div class="user-info">
            <div class="username">{{ request.sender.username }}</div>
          </div>
          <div class="actions">
            <el-button @click="acceptRequest(request.id)" type="success" size="small">接受</el-button>
            <el-button @click="rejectRequest(request.id)" type="danger" size="small">拒绝</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 我的好友列表 -->
    <el-card v-if="friends.length > 0" class="card-container">
      <div class="card-header">我的好友</div>
      <div class="user-list">
        <div v-for="friend in friends" :key="friend.id" class="user-item" @click="goToUserProfile(friend.id)">
          <div class="user-left">
            <img :src="friend.avatar || defaultAvatar" alt="头像" class="user-avatar" />
          </div>
          <div class="user-info">
            <div class="username">{{ friend.username }}</div>
          </div>
          <div class="actions">
            <el-button @click="removeFriendFromList(friend.id)" type="danger" size="small">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 没有好友时的提示 -->
    <el-card v-else class="no-friends-card">
      <div class="no-friends">暂无好友</div>
    </el-card>

    <!-- 分页组件 -->
    <pagination
      :currentPage="currentPage"
      :pageSize="pageSize"
      :total="totalFriends"
      @update:currentPage="handlePageChange"
      @update:pageSize="handleSizeChange"
    />

    <!-- 添加好友对话框 -->
    <add-friend-dialog v-model="addFriendDialogVisible" :userId="currentUser.id"
      @update:visible="addFriendDialogVisible = $event" @friendAdded="fetchPendingRequests" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { getFriendsPage, removeFriend } from '@/api/friendship';
import { getPendingRequests, acceptRequestStatus, rejectRequestStatus } from '@/api/friendRequest';
import type { User } from '@/interface/User';
import AddFriendDialog from '@/components/AddFriendDialog.vue';
import Pagination from '@/components/Pagination.vue'; // 引入分页组件
import { useUserStore } from '@/stores/userStore';
import type { FriendRequest } from '@/interface/FriendRequest';
import { useRoute, useRouter } from 'vue-router';

const addFriendDialogVisible = ref(false); // 控制添加好友对话框
const pendingRequests = ref<FriendRequest[]>([]); // 好友请求列表
const friends = ref<User[]>([]); // 已接受的好友列表
const totalFriends = ref(0); // 好友总数
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
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

// 分页相关的状态
const currentPage = ref(1);
const pageSize = ref(10);
const router = useRouter();

// 点击跳转到用户主页
const goToUserProfile = (authorId: number) => {
  router.push({ name: 'UserProfile', params: { userId: authorId } });
};

// 获取好友列表
const fetchFriends = async () => {
  try {
    const response = await getFriendsPage(currentUser.value.id, currentPage.value, pageSize.value);
    friends.value = response.data;
    totalFriends.value = response.total; // 更新好友总数
  } catch (error) {
    throw error;
  }
};

// 获取好友请求
const fetchPendingRequests = async () => {
  try {
    const response = await getPendingRequests(currentUser.value.id);
    pendingRequests.value = response.data;
  } catch (error) {
    throw error;
  }
};

// 打开添加好友对话框
const openAddFriendDialog = () => {
  addFriendDialogVisible.value = true;
};

// 接受好友请求
const acceptRequest = async (requestId: number) => {
  try {
    await acceptRequestStatus(requestId, "accepted");
    await fetchPendingRequests(); // 更新请求列表
    await fetchFriends(); // 更新好友列表
    ElMessage.success('已接受好友请求');
  } catch (error) {
    throw error;
  }
};

// 拒绝好友请求
const rejectRequest = async (requestId: number) => {
  try {
    await rejectRequestStatus(requestId, "rejected");
    await fetchPendingRequests(); // 更新请求列表
    ElMessage.success('已拒绝好友请求');
  } catch (error) {
    throw error;
  }
};

// 删除好友
const removeFriendFromList = async (friendId: number) => {
  try {
    // 显示确认框
    await ElMessageBox.confirm('确定要删除该好友吗？', '删除好友', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning',
    });

    // 用户确认删除后执行
    await removeFriend(currentUser.value.id, friendId);
    await fetchFriends(); // 更新好友列表
  } catch (error) {
    if (error === 'cancel') {
      throw error;
    }
  };
}

// 处理分页变化
const handlePageChange = (page: number) => {
  currentPage.value = page;
  fetchFriends(); // 更新好友列表
};

const handleSizeChange = (size: number) => {
  pageSize.value = size;
  fetchFriends(); // 更新好友列表
};

// 初始化加载好友请求和好友列表
onMounted(async () => {
  await fetchPendingRequests();
  await fetchFriends();
});
</script>

<style scoped>
.friendship-page {
  width: 1000px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f5f7fa;
}

.add-friend-btn {
  margin-bottom: 20px;
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-container {
  margin-bottom: 20px;
  border-radius: 10px;
  background-color: #fff;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 10px;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid #ddd;
  background-color: #f4f6f9;
  border-radius: 10px 10px 0 0;
}

.user-list {
  max-height: 400px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s, transform 0.2s ease-in-out, box-shadow 0.2s ease;
  cursor: pointer; /* 显示手形指针 */
}

.user-item:hover {
  background-color: #f7f7f7;
  box-shadow: 0px 4px 12px rgba(0, 0, 0, 0.1); /* 提升阴影效果 */
}

.user-info {
  flex-grow: 1;
  padding-left: 15px;
}

.actions {
  display: flex;
  gap: 10px;
}

.username {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.user-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: 2px solid #ddd;
}

.no-requests,
.no-friends {
  text-align: center;
  color: #999;
  font-size: 16px;
  font-style: italic;
}

.no-requests-card,
.no-friends-card {
  margin-top: 20px;
  background-color: #fafafa;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
}

.el-button {
  border-radius: 20px;
}

.el-button--success {
  background-color: #67C23A;
  border-color: #67C23A;
}

.el-button--danger {
  background-color: #F56C6C;
  border-color: #F56C6C;
}
</style>
