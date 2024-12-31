import request from './request';
import { ElMessage } from 'element-plus';
import type { PageBean } from '@/interface/PageBean';
import type { FriendRequest } from '@/interface/FriendRequest';
// 获取好友请求
export const getPendingRequests = async (
    userId: number,
    page: number = 1,
    pageSize: number = 10)
    : Promise<PageBean<FriendRequest>> => {
    try {
        const response = await request.get(`/friendRequest/getPendingRequests/${userId}`, {
            params: {
                userId,
                page,
                pageSize
            }
        });
        const result = response.data;

        if (result.code === 1) {
            return result.data;
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取好友请求失败，请重试');
        throw error;
    }
};



// 发送好友请求
export const addFriendRequest = async (userId: number, friendId: number, message: string): Promise<void> => {
    try {
        const response = await request.post('/friendRequest/sendRequest', {
            senderId: userId,
            receiverId: friendId,
            content: message
        });
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('好友请求已发送');
        } else {
            ElMessage.error(result.msg);
        }
    } catch (error) {
        throw error;
    }
};


// 接受好友请求
export const acceptRequestStatus = async (friendRequestId: number, status: String) => {
    try {
        console.log(friendRequestId, status);
        const response = await request.get('/friendRequest/acceptRequest', {
            params: {
                friendRequestId,
                status
            }
        });
        const result = response.data;
        if (result.code === 1) {
            return result;
        } else {
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('接受好友请求失败，请重试');
        throw error;
    }
};

//拒绝好友请求
export const rejectRequestStatus = async (friendRequestId: number, status: String) => {
    try {
        console.log(friendRequestId, status);
        const response = await request.get('/friendRequest/rejectRequest', {
            params: {
                friendRequestId,
                status
            }
        });
        const result = response.data;
        if (result.code === 1) {
            return result;
        }
    } catch (error) {
        ElMessage.error('接受好友请求失败，请重试');
        throw error;
    }
};


