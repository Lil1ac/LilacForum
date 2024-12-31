// 定义消息类型
export type MessageType = 'status' | 'chat'; // 你可以根据需求扩展更多类型

// WebSocket 消息接口
export interface WSMessage<T = any> {
  type: MessageType; // 消息类型
  data: T;           // 消息内容，根据 type 决定类型
}
