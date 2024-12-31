import request from './request';
import { ElMessage } from 'element-plus';
import type { Message } from '@/interface/Message';


// 获取单聊消息接口，支持分页和游标加载
export const fetchMessages = async (
  fromUserId: number,
  toUserId: number,
  cursor: number | null = null, // 游标：首次请求时为 null，后续请求使用最后一条消息的 ID
  pageSize: number = 10 // 每页显示的条数，默认为 20
): Promise<Message[]> => {
  try {
    const response = await request.get(`/imSingle`, {
      params: { 
        fromUserId, 
        toUserId, 
        cursor, 
        pageSize 
      }
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



// 用于加载未读消息数的函数
export const getUnReadNum = async (currentUserId: number) => {
  try {
    const response = await request.get('/imSingle/unReadNums', {
      params: { toUserId: currentUserId }
    });
    const result = response.data;
    if (result.code === 1) {
      // 成功获取到未读消息数
      return result.data;
    } else {
      throw new Error(result.msg);
    }
  } catch (error) {
    console.error('加载未读消息数失败:', error);
  }
};



// 用于获取用户最后一条消息的函数
export const fetchLastMessage = async (fromUserId: number, toUserId: number): Promise<Message | null> => {
  try {
    const response = await request.get('/imSingle/lastMessage', {
      params: { fromUserId, toUserId }
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data; 
    } else {
      return null;
    }
  } catch (error) {
    console.error('加载最后一条消息失败:', error);
    return null; 
  }
};