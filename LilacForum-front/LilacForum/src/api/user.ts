import request from './request';
import { ElMessage } from 'element-plus';
import type { User } from '@/interface/User';

// 获取用户信息
export const getUserInfo = async (userId: number): Promise<User> => {
    try {
        const response = await request.get(`/user/info/${userId}`);
        const result = response.data;
        if (result.code === 1) {
            return result.data;
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取用户信息失败，请重试');
        throw error;
    }
};

// 更新用户信息
export const updateUserInfo = async (user: User): Promise<void> => {
    try {
        const response = await request.put('/user/info', user);
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('用户信息更新成功');
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        throw error;
    }
};



