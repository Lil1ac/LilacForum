
import type { User } from './User';

export interface Friendship {
  id: number; // 好友请求ID
  userId: number; // 发起人ID
  friendId: number; // 接受人ID
  status: FriendshipStatus; // 好友请求状态
  createdAt: string; // 请求创建时间
  updatedAt: string; // 请求更新时间
  user: User; // 发起人的完整信息
  friend: User; // 接受人的完整信息
}

export type FriendshipStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED';
