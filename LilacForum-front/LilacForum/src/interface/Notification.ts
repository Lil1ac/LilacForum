// src/interface/Notification.ts
export interface Notification {
    id: number;             // 通知ID
    userId: number;         // 接收通知的用户ID
    type: NotificationType; // 通知类型
    content: string;        // 通知内容
    isRead: boolean;        // 是否已读
    createdAt: string;      // 通知的创建时间
  }
  
  // 通知类型定义
  export type NotificationType = 'FRIEND_REQUEST' | 'MESSAGE' | 'OTHER';
  