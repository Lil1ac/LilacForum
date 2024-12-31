import request from './request';
import { ElMessage } from 'element-plus';
import type {User} from '../interface/User';
import type { PageBean } from '@/interface/PageBean';
//获取好友列表
export const getFriends = async (
  userId: number, 
  page: number = 1, 
  pageSize: number = 10): Promise<User[]> => {
  try {
    const response = await request.get(`/friendship/getFriends/${userId}`);
    const result = response.data;
    if (result.code === 1) {
      return result.data; // 返回数据
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取好友列表失败，请重试');
    throw error;
  }
};


// 获取好友列表(分页)
export const getFriendsPage = async (
  userId: number, 
  page: number = 1, 
  pageSize: number = 10): Promise<PageBean<User>> => {
  try {
    const response = await request.get(`/friendship/getFriendsPage/${userId}`, {
      params: {
        page,
        pageSize,
      },
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data; // 返回数据
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取好友列表失败，请重试');
    throw error;
  }
};


// 删除好友
export const removeFriend = async (userId: number, friendId: number) => {
  try {
    const response = await request.delete('/friendship/removeFriend', {
      params: {      
        userId,
        friendId
      }
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('已删除好友');
      return result;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('删除好友失败，请重试');
    throw error;
  }
};
