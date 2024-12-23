<template>
  <div class="login-container">
    <transition name="fade">
      <el-card class="login-card" v-if="showForm">
        <el-form :model="credentials" :rules="rules" ref="loginForm" class="login-form">
          <h2 class="form-title">登录</h2>
          <el-form-item prop="username">
            <el-input v-model="credentials.username" placeholder="用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="credentials.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" />
          </el-form-item>
          <el-button type="primary" @click="handleLogin" class="form-button">登录</el-button>
          <p class="form-link" @click="goToRegister">还没有账号？注册</p>
        </el-form>
      </el-card>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { login } from '@/api/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const credentials = ref({
  username: '',
  password: ''
});

const rules = ref({
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
});

const loginForm = ref();


const handleLogin = async () => {
  try {
    const result = await login(credentials.value);
    if (result) {
      // 登录成功后跳转到主页
      router.push({ name: 'Welcome' });
    }
  } catch (error: any) {
    ElMessage.error('登录失败' + error.message);
  }
};

const goToRegister = () => {
  showForm.value = false;
  setTimeout(() => router.push({ name: 'Register' }), 500);
};

const showForm = ref(false);

onMounted(() => {
  showForm.value = true;
});
</script>


<style scoped>
.login-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.login-card {
  width: 360px;
  padding: 40px 30px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.4);
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  backdrop-filter: saturate(180%);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: box-shadow 0.3s ease-in-out;
}

.login-card:hover {
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
}

.form-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 40px;
  text-align: center;
  color: #ffffffcc;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.form-button {
  width: 100%;
  margin-top: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #ffffffcc;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.form-button:hover {
  background-color: rgba(255, 255, 255, 0.3);
  /* 按钮悬停效果 */
  color: #ffffff;
}

.form-link {
  text-align: center;
  margin-top: 15px;
  cursor: pointer;
  color: #80bfff;
  font-weight: 500;
}

.form-link:hover {
  text-decoration: underline;
  color: #ffffff;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}
</style>
