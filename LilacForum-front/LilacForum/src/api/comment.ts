import request from './request';
import type { Comment } from '@/interface/Comment';
import { useUserStore } from '@/stores/userStore';
import type { PageBean } from '@/interface/PageBean';
import { ElMessage } from 'element-plus'; // 导入 ElMessage

// 获取指定帖子的所有评论（分页）
export const getCommentsByPostId = async (
    postId: number,
    page: number = 1,
    pageSize: number = 10,
    sortBy: string = 'asc' // 默认按时间正序
): Promise<PageBean<Comment>> => {
    try {
        const response = await request.get(`/comments/posts/${postId}`, {
            params: {
                page,
                pageSize,
                sortBy
            },
        });
        const result = response.data;
        if (result.code === 1) {
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取评论失败，请重试');
        throw error;
    }
};

// 创建新评论
export const createNewComment = async (postId: number, content: string): Promise<void> => {
    try {
        const userStore = useUserStore();
        const response = await request.post('/comments', {
            postId,
            userId: userStore.userId,
            content
        });
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('评论创建成功');
        } else {
            ElMessage.error(result.msg);
        }
    } catch (error) {
        ElMessage.error('创建评论失败，请重试');
    }
};

// 获取评论的详细信息
export const getCommentById = async (commentId: number): Promise<Comment> => {
    try {
        const response = await request.get(`/comments/${commentId}`);
        const result = response.data;
        if (result.code === 1) {
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取评论详情失败，请重试');
        throw error;
    }
};

// 删除评论
export const deleteCommentById = async (commentId: number): Promise<void> => {
    try {
        const response = await request.delete(`/comments/${commentId}`);
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('评论删除成功');
        } else {
            ElMessage.error(result.msg);
        }
    } catch (error) {
        ElMessage.error('删除评论失败，请重试');
    }
};



// 搜索最近的回复（根据内容或内容加帖子的标题）
export const searchComments = async (
  keyword: string,
  page: number = 1,
  pageSize: number = 10,
  searchType: string = 'content', // 搜索模式，默认根据回复内容
  sortBy: string = 'createdTime'
): Promise<PageBean<Comment>> => {
  try {
    const response = await request.get('/comments/search', {
      params: {
        keyword,
        page,
        pageSize,
        searchType,
        sortBy,
      },
    });
    const result = response.data;
    if (result.code === 1) {
      return result.data;
    } else {
      ElMessage.error(result.msg);
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('搜索回复失败，请重试');
    throw error;
  }
};
