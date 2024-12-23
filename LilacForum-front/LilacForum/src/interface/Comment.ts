export interface Comment {
    id: number;
    postId: number;
    authorId: number;
    author: string;  // 用户名
    avatar: string; //头像
    createdTime: string;
    content: string;
}