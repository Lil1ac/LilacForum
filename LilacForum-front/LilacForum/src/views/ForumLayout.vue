<template>
  <div class="forum-layout">
    <!-- 顶栏 -->
    <el-header class="forum-header">
      <h1 class="forum-title">LilacForum</h1>
      <div class="header-content">
        <el-menu :default-active="activeMenu" class="header-menu" @select="handleMenuSelect" mode="horizontal">
          <el-menu-item index="HomePage">主页</el-menu-item>
          <el-menu-item index="Sections">板块</el-menu-item>
          <el-menu-item index="Announcements">公告</el-menu-item>
          <el-menu-item index="Chat">聊天室</el-menu-item>
        </el-menu>

        <!-- 搜索框 -->
        <div class="search-container">
          <el-input v-model="searchQuery" placeholder="搜索" class="search-input" clearable @keyup.enter="handleSearch()">
            <template #append>
              <el-select v-model="searchMode" class="search-mode-select">
                <el-option label="标题" value="title"></el-option>
                <el-option label="内容" value="content"></el-option>
              </el-select>
              <el-button icon="Search" @click="handleSearch()"></el-button>
            </template>
          </el-input>
        </div>

        <div class="header-right">
          <!-- 用户信息 -->
          <div @click="goToUserProfile" class="user-info">
            <img :src="avatar" alt="User Avatar" class="user-avatar" />
            <span class="username">{{ username }}</span>
          </div>
          <!-- 下拉菜单 -->
          <el-dropdown type="primary" trigger="click" @command="handleCommand">
            <el-icon class="arrow-down-icon">
              <ArrowDown />
            </el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile" :icon="EditPen">修改信息</el-dropdown-item>
                <el-dropdown-item command="myFriend" :icon="User">我的好友</el-dropdown-item>
                <el-dropdown-item command="logout" :icon="SwitchButton">登出</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>
    <el-container class="main-container">
      <!-- 路由内容 -->
      <el-main class="forum-main">
        <!-- <div class="background-video">
          <video autoplay muted loop playsinline>
            <source src="@/assets/misuzu.mp4" type="video/mp4" />
          </video>
        </div> -->
        <!-- <img src="@/assets/lan.png" class="background-image"> -->
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/stores/userStore';
import { getUserInfo } from '@/api/user';
import { ArrowDown, EditPen, SwitchButton, User } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

// 使用 Pinia store
const userStore = useUserStore();
const router = useRouter();

// 从 userStore 中获取用户名和头像
const username = computed(() => userStore.username);
const avatar = computed(() => userStore.avatar);

// 路由高亮
const activeMenu = computed(() => router.currentRoute.value.name as string);

// 搜索框输入内容
const searchQuery = ref('');
const searchMode = ref('title');

// 处理菜单项选择
const handleMenuSelect = (index: string) => {
  const routeMap: { [key: string]: string } = {
    Welcome: 'Welcome',
    HomePage: 'HomePage',
    Sections: 'Sections',
    Announcements: 'Announcements',
    Chat: 'Chat'
  };
  router.push({ name: routeMap[index] });
};

// 处理搜索并跳转
const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({
      name: 'SearchResults',
      query: {
        keyword: searchQuery.value,
        mode: searchMode.value,
      },
    });
    searchQuery.value = '';
  };
}




// 跳转到 UserProfile 页面并显示提示
const goToUserProfile = () => {
  const userId = userStore.userId;
  if (userId) {
    router.push({ name: 'UserProfile', params: { userId } });
  } else {
    ElMessage.error('用户信息未找到');
  }
};

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  if (command === 'logout') {
    userStore.clearUserInfo();
    localStorage.removeItem('token');
    router.push({ name: 'Login' });
  } else if (command === 'profile') {
    router.push({ name: 'Profile' });
  } else if (command === 'myFriend') {
    router.push({ name: 'MyFriend' });
  }
};

// 获取用户信息并更新 userStore
const fetchUserInfo = async () => {
  try {
    const userId = userStore.userId;
    if (userId) {
      const userData = await getUserInfo(userId);
      if (userData.username && userData.avatar) {
        userStore.setUserInfo(userId, userData.username, userData.avatar, userData.role);
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style scoped>
.forum-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-image: url('@/assets/lan.png');
  background-size: cover;
  background-attachment: fixed;
  background-position: center;
}

.forum-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409eff;
  color: white;
  padding: 0 20px;
  height: 60px;
}

.forum-title {
  font-size: 24px;
  font-weight: bold;
  color: white;
}

.header-content {
  display: flex;
  align-items: center;
  flex: 1;
}

.header-menu {
  flex: 1;
  margin-left: 20px;
  background-color: transparent;
  border: none;
}

.search-container {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.search-mode-select {
  width: 75px;
}

.search-input {
  width: 400px;
  display: flex;
  align-items: center;
}

.search-mode-select {
  margin-right: 8px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-right: 20px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  border: 2px solid #ffffff;
  object-fit: cover;
}

.username {
  margin-right: 10px;
  color: #ffffff;
  font-size: 16px;
  font-weight: 500;
}

.arrow-down-icon {
  cursor: pointer;
  color: #ffffff;
  font-size: 18px;
  margin-left: 10px;
}

.main-container {
  margin-top: 60px;
  display: flex;
  flex: 1;
}

.forum-main {
  flex: 1;
  width: 900px;

}

.header-menu .el-menu-item {
  font-size: 16px;
  transition: background-color 0.3s, color 0.3s;
  border-radius: 4px;
  margin: 0 10px;
  color: #ffffff;
  background-color: transparent;
}

.header-menu .el-menu-item:hover {
  background-color: #d3dce6;
}

.header-menu .el-menu-item.is-active {
  background-color: #1687ff;
  color: #ffffff !important;
}

.home-container {
  flex: 1;
  width: 1200px;
  background-color: #ffffff;
}

.background-video {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  z-index: -1;
}

.background-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  position: absolute;
  top: 0;
  left: 0;
  z-index: -1;
}

body {
  background-color: transparent;
  background: none;
}
</style>
