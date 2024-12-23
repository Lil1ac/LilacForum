import request from './request';
import type { Section } from '@/interface/Section';
import { ElMessage } from 'element-plus';

// 获取所有板块
export const getAllSections = async (): Promise<Section[]> => {
    try {
        const response = await request.get('/sections');
        const result = response.data;
        if (result.code === 1) {
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取板块列表失败，请重试');
        throw error;
    }
};

// 获取具体一个板块
export const getSectionDetail = async (sectionId: number) => {
    try {
        const response = await request.get(`/sections/${sectionId}`);
        const result = response.data;
        if (result.code === 1) {
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('获取板块详情失败，请重试');
        throw error;
    }
};

// 添加板块
export const addSection = async (sectionData: Section) => {
    try {
        const response = await request.post('/sections', sectionData);
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('板块添加成功');
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('添加板块失败，请重试');
        throw error;
    }
};

//更新板块
export const updateSection = async (sectionData: Section) => {
    try {
        const response = await request.put(`/sections`, sectionData);
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('更新成功');
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('更新板块失败，请重试');
        throw error;
    }
}

// 删除板块
export const deleteSection = async (sectionId: number) => {
    try {
        const response = await request.delete(`/sections/${sectionId}`);
        const result = response.data;
        if (result.code === 1) {
            ElMessage.success('板块删除成功');
            return result.data;
        } else {
            ElMessage.error(result.msg);
            throw new Error(result.msg);
        }
    } catch (error) {
        ElMessage.error('删除板块失败，请重试');
        throw error;
    }
};
