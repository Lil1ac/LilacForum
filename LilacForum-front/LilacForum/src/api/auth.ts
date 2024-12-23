import request from './request';
import { jwtDecode } from 'jwt-decode';
import { useUserStore } from '@/stores/userStore';
import { ElMessage } from 'element-plus'; // 导入 ElMessage

// 用户注册
export const register = async (user: { username: string; password: string; email: string }) => {
  try {
    const response = await request.post('/auth/register', user);
    const result = response.data;

    if (result.code === 1) {
      ElMessage.success('注册成功');
      return result.data;
    } else {
      ElMessage.error(result.msg);
    }
  } catch (error) {
    ElMessage.error('注册失败');
  }
};

// 用户登录
export const login = async (credentials: { username: string; password: string }) => {
  try {
    const response = await request.post('/auth/login', credentials);
    const result = response.data;

    if (result.code === 1) {
      // 从响应中获取 JWT 令牌
      const token = result.data;

      if (typeof token === 'string') {
        // 将令牌存储到 localStorage 中
        localStorage.setItem('token', token);

        // 解码令牌以提取用户名
        const decodedToken: any = jwtDecode(token);
        const userId = decodedToken.userId;

        // 更新 Pinia store
        const userStore = useUserStore();
        userStore.userId = userId;

        ElMessage.success('登录成功');
        return result.data;
      } else {
        ElMessage.error('Token 不是有效的字符串');
      }
    } else {
      ElMessage.error(result.msg);
    }
  } catch (error: any) {
    ElMessage.error('登录失败');
  }
};
