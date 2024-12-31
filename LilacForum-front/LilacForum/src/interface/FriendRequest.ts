
import type { User } from './User';
export interface FriendRequest {
    id: number;           // 请求 ID
    senderId: number;     // 发送者 ID
    receiverId: number;   // 接受者 ID
    status: 'pending' | 'accepted' | 'rejected';  // 请求状态
    createTime: string;   // 请求发送时间
    updateTime: string;   // 请求更新时间
    sender: User;         // 发送者信息
  }
  