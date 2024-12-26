import request from './request';
import { ElMessage } from 'element-plus';
import type { Message } from '@/interface/Message';


// 获取单聊消息接口
export const fetchMessages = async (fromUser: string, toUser: string): Promise<Message[]> => {
    try {
        const response = await request.get('/imSingle', {
            params: { fromUser, toUser }
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
// 获取未读消息数量接口
export const fetchUnreadNums = async (toUser: string): Promise<number> => {
    try {
        const response = await request.get('/imSingle/unReadNums', {
            params: { toUsername: toUser }
        });
        const result = response.data;
        if (result.code === 1) {
            return result.data.unreadNums; // 假设未读消息数量字段为 `unreadNums`
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取未读消息数量失败');
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
