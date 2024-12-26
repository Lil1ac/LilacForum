export interface Message {
    id: number;         // 消息ID
    content: string;    // 消息内容
    fromUser: string;   // 发送者用户名
    fromAvatar: string; // 发送者头像
    time: string;       // 消息时间
    type: string;       // 消息类型 ('text', 'img', 'file' 等)
    toUser: string;     // 接收者用户名
    toAvatar: string;   // 接收者头像
    isRead: number;     // 消息是否已读，0: 未读，1: 已读
}
