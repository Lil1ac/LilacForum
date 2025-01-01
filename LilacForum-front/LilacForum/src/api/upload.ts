import request from './request';
import { ElMessage } from 'element-plus';

// 上传头像
export const uploadImage = async (file: File): Promise<string> => {
    const formData = new FormData();
    formData.append('image', file); // 这里的 'image' 应与后端的 MultipartFile 参数名一致
  
    try {
      const response = await request.post('/upload/image', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      });
      const result = response.data;
      if (result.code === 1) {
        return result.data; // 假设返回的是头像的 URL
      } else {
        ElMessage.error(result.msg);
        throw new Error(result.msg);
      }
    } catch (error) {
      ElMessage.error('文件上传失败');
      throw error;
    }
  };
