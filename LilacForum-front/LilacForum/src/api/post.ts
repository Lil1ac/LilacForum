import request from './request';
import type { Post } from '@/interface/Post';
import { useUserStore } from '@/stores/userStore';
import type { PageBean } from '@/interface/PageBean';
import { ElMessage } from 'element-plus';

// 获取指定板块的所有帖子（分页、排序、搜索）
export const getPostsBySectionId = async (
  sectionId: number,
  page: number = 1,
  pageSize: number = 10,
  sortBy: string = 'createdTime',
  begin?: string, // 日期范围的开始时间
  end?: string // 日期范围的结束时间
): Promise<PageBean<Post>> => {
  try {
    const response = await request.get(`/posts/sections/${sectionId}`, {
      params: {
        page,
        pageSize,
        sortBy,
        begin,
        end,
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
    ElMessage.error('获取帖子失败，请重试');
    throw error;
  }
};


// 获取用户发布的帖子列表（分页）
export const getUserPosts = async (
  userId: number,
  page: number = 1,
  pageSize: number = 10
): Promise<Post[]> => {
  try {
    const response = await request.get(`/posts/user/${userId}`, {
      params: {
        page,
        pageSize
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
    ElMessage.error('获取用户帖子失败，请重试');
    throw error;
  }
};


// 获取指定帖子的详细信息
export const getPostDetail = async (postId: number): Promise<Post> => {
  try {
    const response = await request.get(`/posts/${postId}`);
    const result = response.data;
    if (result.code === 1) {
      return result.data;
    } else {
      ElMessage.error(result.msg);
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('获取帖子详情失败，请重试');
    throw error;
  }
};

// 创建新帖子
export const createNewPost = async (sectionId: number, postData: { title: string; content: string }): Promise<void> => {
  try {
    const userStore = useUserStore();
    const response = await request.post('/posts', {
      sectionId,
      userId: userStore.userId,
      ...postData
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('帖子创建成功');
    } else {
      ElMessage.error(result.msg);
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('创建帖子失败，请重试');
    throw error;
  }
};

// 删除帖子
export const deletePostById = async (postId: number): Promise<void> => {
  try {
    const userStore = useUserStore();
    const response = await request.delete(`/posts/${postId}`, {
      data: {
        userId: userStore.userId, // 确保请求中包含用户 ID，用于验证权限
      },
    });
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('帖子删除成功');
    } else {
      ElMessage.error(result.msg);
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('删除帖子失败，请重试');
    throw error;
  }
};


// 修改帖子
export const updatePost = async (postData: Post): Promise<void> => {
  try {
    const response = await request.put(`/posts`, postData);
    const result = response.data;
    if (result.code === 1) {
      ElMessage.success('帖子修改成功');
    } else {
      ElMessage.error(result.msg);
      throw new Error(result.msg);
    }
  } catch (error) {
    ElMessage.error('修改帖子失败，请重试');
    throw error;
  }
};


// 搜索所有帖子（根据标题或标题加内容）
export const searchPosts = async (
  keyword: string,
  page: number = 1,
  pageSize: number = 10,
  searchType: string = 'title', // 搜索模式
  sortBy: string = 'createdTime'
): Promise<PageBean<Post>> => {
  try {
    const response = await request.get('/posts/search', {
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
    ElMessage.error('搜索帖子失败，请重试');
    throw error;
  }
};

