<template>
  <div class="user-profile">
    <el-card class="profile-card">
      <!-- 当前头像展示 -->
      <div class="card-header">
        <div class="avatar-container" @click="openAvatarModal">
          <el-avatar :src="userInfo.avatar" class="avatar"></el-avatar>
        </div>
        <div class="user-info">
          <h2>{{ userInfo.username }}</h2>
        </div>
      </div>

      <!-- 头像编辑弹窗 -->
      <el-dialog title="编辑头像" v-model="showAvatarModal">
        <ImageUpload ref="imageEditor" :initial-image="imageUrl" :aspectRatio="1" @save="handleAvatarSave"
          @cancel="handleAvatarCancel" />
        <!-- 操作按钮 -->
        <div class="action-buttons">
          <el-button type="primary" @click="triggerConfirmUpload">保存头像</el-button>
          <el-button @click="triggerCancelUpload">取消</el-button>
        </div>
      </el-dialog>

      <!-- 其他用户信息表单 -->
      <el-form :model="userInfo" :rules="rules" label-width="120px" class="form">
        <el-form-item label="用户名">
          <el-input v-model="userInfo.username" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userInfo.email" :disabled="!isEditing"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="userInfo.gender" :disabled="!isEditing">
            <el-option label="男" value="男"></el-option>
            <el-option label="女" value="女"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input v-model="userInfo.age" type="number" :disabled="!isEditing"></el-input>
        </el-form-item>
        <el-form-item label="职业">
          <el-input v-model="userInfo.profession" :disabled="!isEditing"></el-input>
        </el-form-item>
        <el-form-item label="爱好">
          <el-input v-model="userInfo.hobby" :disabled="!isEditing"></el-input>
        </el-form-item>
        <el-form-item label="简介">
          <el-input type="textarea" v-model="userInfo.bio" :disabled="!isEditing"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="isEditing ? saveUserInfo() : startEditing()">
            {{ isEditing ? '保存' : '修改个人信息' }}
          </el-button>
          <el-button v-if="isEditing" @click="cancelEditing">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import 'cropperjs/dist/cropper.css';
import { useUserStore } from '@/stores/userStore';
import { getUserInfo, updateUserInfo } from '@/api/user';
import type { User } from '@/interface/User';

const userInfo = ref<User>({
  uid: 0,
  username: '',
  password: '',
  email: '',
  gender: '',
  age: 0,
  profession: '',
  hobby: '',
  bio: '',
  avatar: 'https://via.placeholder.com/150',
  role: ''

});

const isEditing = ref(false);

const rules = ref({
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: ['blur', 'change'] }
  ]
});

const userStore = useUserStore();

const fetchUserInfo = async () => {
  try {
    const userId = userStore.userId;
    if (userId === null) {
      ElMessage.error('用户 ID 无效');
      return;
    }
    userInfo.value = await getUserInfo(userId);
    imageUrl.value = userInfo.value.avatar; // 设置初始图片为当前头像
  } catch (error) {
    throw error
  }
};


import ImageUpload from '@/components/ImageUpload.vue';

const showAvatarModal = ref(false);
const imageUrl = ref<string>(''); // 用于控制显示加号图标或裁剪图
const imageEditor = ref<InstanceType<typeof ImageUpload> | null>(null);

// 打开头像编辑弹窗
const openAvatarModal = () => {

  imageEditor.value?.resetImageUrl(userInfo.value.avatar);
  showAvatarModal.value = true;
};

//上传后的逻辑
const handleAvatarSave = async (avatarUrl: string) => {
  userInfo.value.avatar = avatarUrl;
  //更新用户信息
  await updateUserInfo(userInfo.value);
  showAvatarModal.value = false;
  if (userStore.userId) {
    userStore.setUserInfo(userStore.userId, userInfo.value.username, avatarUrl, userStore.role);
  }
  showAvatarModal.value = false;
};

//取消后的逻辑
const handleAvatarCancel = () => {
  showAvatarModal.value = false;
}

const triggerConfirmUpload = () => {
  if (imageEditor.value) {
    imageEditor.value.confirmUpload((backgroundUrl) => {
      // 设置头像 URL
      handleAvatarSave(backgroundUrl);
    });
  }
};


const triggerCancelUpload = () => {
  if (imageEditor.value) {
    imageEditor.value.cancelUpload();
  }
};


const saveUserInfo = async () => {
  try {
    await updateUserInfo(userInfo.value);
    isEditing.value = false;
  } catch (error) {
    throw error;
  }
};

const startEditing = () => {
  isEditing.value = true;
};

const cancelEditing = () => {
  isEditing.value = false;
};

onMounted(() => {
  fetchUserInfo();
});

</script>

<style scoped>
.user-profile {
  padding: 40px;
  display: flex;
  justify-content: center;
}

.profile-card {
  width: 100%;
  max-width: 1200px;
  background: #ffffff;
  border-radius: 15px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  animation: fadeIn 1s ease-in-out;
  position: relative;
}

.card-header {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #007bff;
  color: #ffffff;

}

.avatar-container {
  position: relative;
  cursor: pointer;
}

.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s, box-shadow 0.3s;
}


.avatar:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
}

.user-info {
  margin-left: 20px;
}

.action-buttons {
  margin-top: 20px;
  text-align: center;
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 10px;
}

.form {
  padding: 20px;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
}
</style>
