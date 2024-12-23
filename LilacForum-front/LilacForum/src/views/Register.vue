<template>
  <div class="register-container">
    <transition name="fade">
      <el-card class="register-card" v-if="showForm">
        <el-form :model="form" :rules="rules" ref="registerForm" class="register-form">
          <h2 class="form-title">注册</h2>
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" prefix-icon="el-icon-user" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="el-icon-lock" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="el-icon-lock" />
          </el-form-item>
          <el-form-item prop="email">
            <el-input v-model="form.email" placeholder="邮箱" prefix-icon="el-icon-mail" />
          </el-form-item>
          <el-button type="primary" @click="handleRegister" class="form-button">注册</el-button>
          <p class="form-link" @click="goToLogin">已有账号？登录</p>
        </el-form>
      </el-card>
    </transition>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { register } from '@/api/auth';
import { ElMessage } from 'element-plus';

const router = useRouter();
const form = ref({
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
});

const rules = ref({
  username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
  email: [
    { required: true, message: '邮箱不能为空', trigger: 'blur' },
    { pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/, message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: '密码不能为空', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '确认密码不能为空', trigger: 'blur' },
    {
      validator: (rule: any, value: string, callback: Function) => {
        if (value !== form.value.password) {
          callback(new Error('确认密码与密码不匹配'));
        } else {
          callback();
        }
      }, trigger: 'blur'
    }
  ]
});

const registerForm = ref();
const showForm = ref(false);

const handleRegister = async () => {
  await registerForm.value.validate(async (valid: boolean) => {
    if (valid) {
      try {
        const result = await register(form.value);
        if (result) {
          // 注册成功后，跳转到登录页面
          router.push('/login');
        }
      } catch (error: any) {
        ElMessage.error('注册失败，请重试');
      }
    } else {
      ElMessage.warning('请检查表单填写是否正确');
    }
  });
};


const goToLogin = () => {
  showForm.value = false;
  setTimeout(() => router.push('/login'), 500);
};

onMounted(() => {
  showForm.value = true;
});
</script>

<style scoped>
.register-container {
  position: relative;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}

.register-card {
  width: 360px;
  padding: 40px 30px;
  border-radius: 10px;
  background: rgba(0, 0, 0, 0.4);
  /* 背景变暗 */
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.3);
  transition: box-shadow 0.3s ease-in-out;
}

.register-card:hover {
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.5);
}

.form-title {
  font-size: 26px;
  font-weight: bold;
  margin-bottom: 40px;
  text-align: center;
  color: #ffffffcc;
  /* 半透明白色 */
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.form-button {
  width: 100%;
  margin-top: 20px;
  background-color: rgba(255, 255, 255, 0.2);
  /* 按钮背景透明 */
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #ffffffcc;
  /* 按钮文本半透明白色 */
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

.el-input__inner::placeholder {
  color: #ffffff99;
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
