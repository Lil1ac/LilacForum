import request from './request';
import { ElMessage } from 'element-plus';

// 获取好友请求
export const getPendingRequests = async (userId: number) => {
  try {
    const response = await request.get('/friendship/getFriendRequests', {
      params: { userId }
    });
    if (Array.isArray(response.data)) {
      response.data = [];
    }
    const result = response.data;
    if (result.code === 1) {
      return result.data; // 返回数据
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取好友请求失败，请重试');
    throw error;
  }
};

// 获取好友列表
export const getFriends = async (userId: number) => {
  try {
    const response = await request.get('/friendship/getFriends', {
      params: { userId }
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

// 发送好友请求
export const addFriendRequest = async (userId: number, friendId: number): Promise<void> => {
  try {
    const response = await request.get('/friendship/sendRequest', {
      params: {
        userId,
        friendId
      }
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('好友请求已发送');
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('发送好友请求失败，请重试');
    throw error;
  }
};


// 接受好友请求
export const acceptFriendRequest = async (userId: number, friendId: number) => {
  try {
    console.log(userId, friendId);
    const response = await request.post('/friendship/acceptRequest', {
      userId,
      friendId
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('已接受好友请求');
      return result;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('接受好友请求失败，请重试');
    throw error;
  }
};

// 拒绝好友请求
export const rejectFriendRequest = async (userId: number, friendId: number) => {
  try {
    const response = await request.post('/friendship/rejectRequest', {
      userId,
      friendId
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('已拒绝好友请求');
      return result;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('拒绝好友请求失败，请重试');
    throw error;
  }
};

// 删除好友
export const removeFriend = async (userId: number, friendId: number) => {
  try {
    const response = await request.post('/friendship/removeFriend', {
      userId,
      friendId
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
