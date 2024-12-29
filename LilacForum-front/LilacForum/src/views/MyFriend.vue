<template>
  <div class="friendship-page">
    <!-- 添加好友按钮 -->
    <el-button @click="openAddFriendDialog" type="primary" class="add-friend-btn">添加好友</el-button>

    <!-- 好友请求列表 -->
    <el-card v-if="pendingRequests && pendingRequests.length > 0" class="card-container">
      <div class="card-header">好友请求</div>
      <div class="user-list">
        <div v-for="request in pendingRequests" :key="request.id" class="user-item">
          <div class="user-left">
            <img :src="request.user.avatar || defaultAvatar" alt="头像" class="user-avatar" />
          </div>
          <div class="user-info">
            <div class="username">{{ request.user.username }}</div>
          </div>
          <div class="actions">
            <el-button @click="acceptRequest(request.id)" type="success" size="mini">接受</el-button>
            <el-button @click="rejectRequest(request.id)" type="danger" size="mini">拒绝</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 好友列表 -->
    <el-card class="card-container">
      <div class="card-header">我的好友</div>
      <div class="user-list">
        <div v-for="friend in friends" :key="friend.id" class="user-item">
          <div class="user-left">
            <img :src="friend.avatar || defaultAvatar" alt="头像" class="user-avatar" />
          </div>
          <div class="user-info">
            <div class="username">{{ friend.username }}</div>
          </div>
          <div class="actions">
            <el-button @click="removeFriendFromList(friend.id)" type="danger" size="mini">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 添加好友对话框 -->
    <add-friend-dialog 
      v-model="addFriendDialogVisible" 
      :userId="currentUser.id" 
      @update:visible="addFriendDialogVisible = $event" 
      @friendAdded="fetchPendingRequests" 
    />
  </div>

</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { getFriends, getPendingRequests, acceptFriendRequest, rejectFriendRequest, removeFriend } from '@/api/friendship';  // 引入 API
import type { User } from '@/interface/User';
import type { Friendship } from '@/interface/Friendship';
import AddFriendDialog from '@/components/AddFriendDialog.vue';
import { useUserStore } from '@/stores/userStore';

const addFriendDialogVisible = ref(false); // 控制添加好友对话框
const pendingRequests = ref<Friendship[]>([]); // 好友请求列表
const friends = ref<User[]>([]); // 已接受的好友列表
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


// 获取好友列表
const fetchFriends = async () => {
  try {
    const response = await getFriends(currentUser.value.id);
    friends.value = response.data;
  } catch (error) {
    ElMessage.error('获取好友列表失败，请重试');
  }
};
// 打开添加好友对话框
const openAddFriendDialog = () => {
  console.log('open add friend dialog');
  addFriendDialogVisible.value = true;
};

// 接受好友请求
const acceptRequest = async (requestId: number) => {
  try {
    await acceptFriendRequest(currentUser.value.id, requestId);
    fetchPendingRequests(); // 更新请求列表
    fetchFriends(); // 更新好友列表
  } catch (error) {
    ElMessage.error('接受好友请求失败，请重试');
  }
};

// 拒绝好友请求
const rejectRequest = async (requestId: number) => {
  try {
    await rejectFriendRequest(currentUser.value.id, requestId);
    fetchPendingRequests(); // 更新请求列表
  } catch (error) {
    ElMessage.error('拒绝好友请求失败，请重试');
  }
};

// 删除好友
const removeFriendFromList = async (friendId: number) => {
  try {
    await removeFriend(currentUser.value.id, friendId);
    fetchFriends(); // 更新好友列表
  } catch (error) {
    ElMessage.error('删除好友失败，请重试');
  }
};

// 初始化加载好友请求和好友列表
onMounted(async () => {
  await fetchPendingRequests();
  await fetchFriends();
});

// 获取好友请求
const fetchPendingRequests = async () => {
  try {
    const response = await getPendingRequests(currentUser.value.id);
    pendingRequests.value = response.data;
  } catch (error) {
    ElMessage.error('获取好友请求失败，请重试');
  }
};


</script>

<style scoped>
.friendship-page {
  padding: 20px;
}

.add-friend-btn {
  margin-bottom: 20px;
}

.card-container {
  margin-bottom: 20px;
}

.user-list {
  max-height: 400px;
  overflow-y: auto;
}

.user-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.user-info {
  flex-grow: 1;
  padding-left: 10px;
}

.actions {
  display: flex;
  gap: 10px;
}

.username {
  font-size: 16px;
  font-weight: bold;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>
