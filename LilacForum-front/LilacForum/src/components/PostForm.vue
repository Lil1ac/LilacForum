<template>
    <el-form label-width="80px">
        <el-form-item label="标题" :error="titleError">
            <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="内容" :error="contentError" class="post-content">
            <!-- 使用封装好的 RichTextEditor 组件 -->
            <RichTextEditor ref="richTextEditor" v-model="formData.content" />
        </el-form-item>
        <div class="dialog-footer">
            <el-button @click="onCancel">取消</el-button>
            <el-button type="primary" @click="submitForm" :loading="submitting">确认</el-button>
        </div>
    </el-form>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue';
import RichTextEditor from './RichTextEditor.vue';

const props = defineProps({
    initialData: {
        type: Object as () => { title: string; content: string },
        required: true
    },
    submitting: Boolean
});

const emits = defineEmits(['submit', 'cancel']);

const formData = ref({ ...props.initialData });

const titleError = ref<string | null>(null);
const contentError = ref<string | null>(null);
const submitting = ref<boolean>(props.submitting || false);
const richTextEditor = ref<typeof RichTextEditor>(); // 添加对 RichTextEditor 的引用

const validateForm = () => {
    let isValid = true;
    if (!formData.value.title) {
        titleError.value = '标题不能为空';
        isValid = false;
    } else if (formData.value.title.length > 50) {
        titleError.value = '标题不能超过50个字';
        isValid = false;
    } else {
        titleError.value = null;
    }

    if (!formData.value.content.trim()) {
        contentError.value = '内容不能为空';
        isValid = false;
    } else {
        contentError.value = null;
    }

    return isValid;
};

const submitForm = () => {
    if (validateForm()) {
        submitting.value = true;
        emits('submit', { ...formData.value });
        formData.value.title = '';
        richTextEditor.value?.clearContent();
        submitting.value = false;
    }
};

const onCancel = () => {
    emits('cancel');
    // 清除富文本编辑器内容
    formData.value.title = '';
    formData.value.content = '';
    richTextEditor.value?.clearContent();
};

// 深拷贝 props.initialData，确保正确更新 formData
watch(
    () => props.initialData,
    (newData) => {
        console.log('initialData', props.initialData)
        console.log('newData', newData);
        formData.value.title = newData.title;
        formData.value.content = newData.content;
        richTextEditor.value?.setContent(newData.content);
    },
);
</script>

<style scoped>
.post-content {
    overflow: hidden;
    max-width: 100%;
}

.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>
