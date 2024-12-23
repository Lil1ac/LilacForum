<template>
    <el-form :model="formData" label-width="80px">
        <el-form-item label="标题" :error="titleError">
            <el-input v-model="formData.title" />
        </el-form-item>
        <el-form-item label="描述" :error="descriptionError">
            <el-input type="textarea" v-model="formData.description" :rows="5" />
        </el-form-item>
        <el-form-item label="背景图片">
            <ImageUpload ref="imageEditor" :initial-image="formData.background" @save="handleBackgroundSave"
                @close="handleBackgroundCancel"/>
        </el-form-item>
        <div class="dialog-footer">
            <el-button @click="onCancel">取消</el-button>
            <el-button type="primary" @click="onSubmit" :loading="submitting">确认</el-button>
        </div>
    </el-form>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue';
import ImageUpload from './ImageUpload.vue';

const props = defineProps({
    initialData: {
        type: Object as () => { title: string; description: string; background: string },
        required: true
    },
    submitting: Boolean
});

const emits = defineEmits(['submit', 'cancel']);

const formData = ref({ ...props.initialData });
const titleError = ref<string | null>(null);
const descriptionError = ref<string | null>(null);

const validateForm = () => {
    let isValid = true;

    if (!formData.value.title) {
        titleError.value = '板块标题不能为空';
        isValid = false;
    } else if (formData.value.title.length > 20) {
        titleError.value = '板块标题不能超过20个字';
        isValid = false;
    } else {
        titleError.value = null;
    }

    if (!formData.value.description) {
        descriptionError.value = '描述不能为空';
        isValid = false;
    } else {
        descriptionError.value = null;
    }

    return isValid;
};


//获取到imageEditor组件
const imageEditor = ref();


const onCancel = () => {
    if (imageEditor.value) {
        imageEditor.value.resetCropper(); // 确保调用重置方法
    }
    emits('cancel');
}

const onSubmit = async () => {
    if (validateForm()) {
        try {
            await new Promise<void>((resolve) => {
                imageEditor.value?.confirmUpload((backgroundUrl: string) => {
                    console.log('onSubmit 被调用 backgroundUrl:', backgroundUrl);

                    if (backgroundUrl) {
                        handleBackgroundSave(backgroundUrl);
                    } else {
                        console.warn('背景图片 URL 为空，无法处理');
                    }
                    resolve();
                });
            });
            console.log('onSubmit 被调用 formData:', formData.value);
            emits('submit', { ...formData.value });
            formData.value = { title: '', description: '', background: '' };
        } catch (error) {
            console.error('提交失败:', error);
        }
    }
};


//上传后的逻辑
const handleBackgroundSave = (backgroundUrl: string) => {
    console.log('handleBackgroundSave 被调用 URL:', backgroundUrl); // 确认 URL
    formData.value.background = backgroundUrl;
};

//取消后的逻辑
const handleBackgroundCancel = () => {
};


watch(
    () => props.initialData,
    (newData) => {
        console.log('更新 initialData:', newData);
        formData.value = { ...newData };
    },
    { immediate: true }
);

const resetSectionForm = () => {
    formData.value = { title: '', description: '', background: '' };
    if (imageEditor.value) {
        imageEditor.value.resetCropper();
    }
};

defineExpose({ resetSectionForm });
</script>

<style scoped>
.dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}
</style>