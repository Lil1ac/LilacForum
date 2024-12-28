import request from './request';
import { ElMessage } from 'element-plus';
import type { Message } from '@/interface/Message';


// 获取单聊消息接口
export const fetchMessages = async (fromUserId: number, toUserId: number): Promise<Message[]> => {
  try {
    const response = await request.get(`/imSingle`, {
      params: { fromUserId, toUserId }
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data; // 返回消息列表
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取消息失败，请重试');
    throw error;
  }
};



// 发送消息接口
export const sendMessage = async (message: Message): Promise<void> => {
  try {
    const response = await request.post('/imSingle/send', message); // 发送消息
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('消息发送成功');
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('消息发送失败，请重试');
    throw error;
  }
};


// 清空正在聊的用户的未读消息数
export const setUnReadNums = async (currentUserId: number, chatUserId: number) => {
  try {
    // 调用后端接口来清空未读消息数
    await request.get('/imSingle', {
      params: { fromUserId: currentUserId, toUserId: chatUserId }
    });
  } catch (error) {
    console.error('设置未读消息数失败:', error);
  }
};

// 用于加载未读消息数的函数
export const loadUnReadNums = async (currentUserId: number) => {
  try {
    const response = await request.get('/imSingle/unReadNums', {
      params: { toUserId: currentUserId }
    });
  } catch (error) {
    console.error('加载未读消息数失败:', error);
  }
};