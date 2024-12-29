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


// 根据角色获取用户列表
export const getUsersByRole = async (role: string, fromUser: string): Promise<User[]> => {
    try {
        // 确保通过 params 传递查询参数
        const response = await request.get('/user/info/role', {
            params: { role, fromUser } // 传递角色和当前用户
        });
        const result = response.data;
        if (result.code === 1) {
            // 按条件过滤用户
            return result.data.filter(
                (v: User) => `${v.role}_${v.username}` !== fromUser && v.role === role
            );
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取用户列表失败，请重试');
        throw error;
    }
};

//根据用户名查找用户
export const getUserByUsername = async (username: string): Promise<User> => {
    try {
        const response = await request.get('/user/info/username', {
            params: { username }
        });
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

