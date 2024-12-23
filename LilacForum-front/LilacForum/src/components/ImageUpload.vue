<template>
    <div class="image-editor">
        <!-- 头像上传和裁剪区域 -->
        <div class="image-preview-container">
            <div class="crop-container">
                <!-- 已上传头像 -->
                <div v-if="imageUrl" class="canvas-container">
                    <canvas ref="canvas" class="crop-canvas"></canvas>
                    <!-- 修改图片按钮 -->
                    <el-upload class="change-button" action="#" :auto-upload="false" :show-file-list="false"
                        @change="handleFileChange">
                        <el-button>修改图片</el-button>
                    </el-upload>
                </div>
                <!-- 未上传头像 -->
                <el-upload v-else class="image-uploader" action="#" :auto-upload="false" :show-file-list="false"
                    :on-change="handleFileChange">
                    <el-icon class="image-uploader-icon">
                        <Plus />
                    </el-icon>
                </el-upload>
            </div>
            <!-- 头像预览 -->
            <div class="preview-container">
                <div class="preview">
                    <img :src="croppedImageUrl" class="preview-image large" />
                </div>
                <div class="preview">
                    <img :src="croppedImageUrl" class="preview-image small" />
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, defineExpose, watch, onMounted, defineProps, defineEmits } from 'vue';
import Cropper from 'cropperjs';
import 'cropperjs/dist/cropper.css';
import { ElMessage } from 'element-plus';
import { uploadImage } from '@/api/upload';
import type { UploadFile } from 'element-plus';
import { nextTick } from 'vue';


const imageUrl = ref<string>(''); // 用于控制显示加号图标或裁剪图
const canvas = ref<HTMLCanvasElement | null>(null);
const croppedImageUrl = ref<string>('');
const cropper = ref<Cropper | null>(null);


const props = defineProps({
    initialImage: String, // 接收父组件传入的初始图片
    aspectRatio: Number, // 接收父组件传入的裁剪比例
});

const emits = defineEmits(['save', 'cancel']);

const beforeUpload = (rawFile: File) => {
    const isJpgOrPng = rawFile.type === 'image/jpeg' || rawFile.type === 'image/png';
    if (!isJpgOrPng) {
        ElMessage.error('头像格式必须为 JPG 或 PNG!');
        return false;
    } else if (rawFile.size / 1024 / 1024 > 2) {
        ElMessage.error('头像大小不能超过 2MB!');
        return false;
    }
    return true;
};

// 点击上传头像
const handleFileChange = (uploadFile: UploadFile) => {
    const file = uploadFile.raw;
    if (!file || !beforeUpload(file)) {
        return false;
    }
    const reader = new FileReader();
    reader.onload = (e) => {
        if (e.target?.result) {
            imageUrl.value = e.target.result as string; // 设置 imageUrl 显示裁剪区域
            initCropper(e.target.result as string);
        }
    };
    reader.readAsDataURL(file);
};

// 初始化裁剪区域
const initCropper = (src: string) => {
    const img = new Image();
    img.crossOrigin = "anonymous"; // 设置为允许跨域请求
    img.src = src;
    img.onload = () => {
        nextTick(() => {
            if (canvas.value) {
                const ctx = canvas.value.getContext('2d');
                if (ctx) {
                    canvas.value.width = img.width;
                    canvas.value.height = img.height;
                    ctx.drawImage(img, 0, 0);

                    if (cropper.value) cropper.value.destroy();

                    cropper.value = new Cropper(canvas.value, {
                        aspectRatio: props.aspectRatio || NaN, // 使用父组件传入的裁剪比例，默认1（正方形）
                        viewMode: 1,
                        crop: updatePreview,
                        ready() {
                            // 设置默认裁剪框为全图
                            const containerData = cropper.value?.getContainerData();
                            const canvasData = cropper.value?.getCanvasData();

                            if (containerData && canvasData) {
                                cropper.value?.setCropBoxData({
                                    left: 0,
                                    top: 0,
                                    width: canvasData.width,
                                    height: canvasData.height,
                                });
                            }
                        }
                    });
                }
            }
        });
    };
};
// 更新预览图
const updatePreview = () => {
    if (cropper.value) {
        const croppedCanvas = cropper.value.getCroppedCanvas();
        croppedImageUrl.value = croppedCanvas.toDataURL();

        // 动态调整预览图的宽高
        const aspectRatio = croppedCanvas.width / croppedCanvas.height;
        document.querySelectorAll('.preview-image').forEach((img) => {
            const imageElement = img as HTMLImageElement;
            imageElement.style.objectFit = 'contain';
            imageElement.style.width = aspectRatio >= 1 ? '100%' : 'auto';
            imageElement.style.height = aspectRatio < 1 ? '100%' : 'auto';
        });
    }
};


// 确认上传(交给父组件的其他组件调用)
const confirmUpload = async (callback: (url: string) => void) => {
    if (cropper.value) {
        const croppedCanvas = cropper.value.getCroppedCanvas();
        croppedCanvas.toBlob(async (blob) => {
            if (blob) {
                try {
                    const file = new File([blob], 'image.jpg', { type: 'image/jpeg' });
                    const imageUrl = await uploadImage(file);
                    callback(imageUrl); // 传递 URL 到父组件
                    resetCropper();
                } catch (error) {
                    throw error;
                }
            }
        });
    }
};

// 取消上传(交给父组件的其他组件调用)
const cancelUpload = () => {
    resetCropper();
    emits('cancel');//取消后的逻辑
};

// 重置裁剪器
const resetCropper = () => {
    if (cropper.value) {
        cropper.value.destroy();
        cropper.value = null;
    }
    croppedImageUrl.value = ''; // 清空预览图
    imageUrl.value = ''; // 重置为上传区域
};


//重置图片url
const resetImageUrl = (newImageUrl: string) => {
    imageUrl.value = newImageUrl;
    initCropper(imageUrl.value);
};


defineExpose({
    confirmUpload,
    cancelUpload,
    resetCropper,
    resetImageUrl
});

onMounted(() => {
    if (props.initialImage) {
        imageUrl.value = props.initialImage;
        console.log(imageUrl.value);
        initCropper(props.initialImage);
    }
});

watch(
    () => props.initialImage,
    (newImage) => {
        if (newImage) {
            imageUrl.value = newImage;
            initCropper(newImage);
        }
    },
    { immediate: true } // 确保组件初始化时立即加载图片
);


</script>

<style scoped>
.image-editor {
    display: flex;
    align-items: center;
    flex-direction: column;
}

.image-preview-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.crop-container {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    margin-right: 20px;
    height: 280px;
    /* 设置固定高度 */
}

.canvas-container {
    width: 250px;
    height: 250px;
}

.crop-canvas {
    width: 100%;
    height: 100%;
}

.change-button {
    margin-top: 10px;
}

.image-uploader {
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    transition: var(--el-transition-duration-fast);
}

.image-uploader:hover {
    border-color: var(--el-color-primary);
}

.image-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 250px;
    height: 250px;
}

.preview-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-left: 20px;
    gap: 30px;

}

.preview {
    margin: 10px 0;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 1px dashed var(--el-border-color);
    overflow: hidden;
}

.preview-image {
    border: 1px dashed var(--el-border-color);
    object-fit: contain;
    max-width: 100%;
    max-height: 100%;
}


.large {
    max-width: 250px;
    max-height: 250px;
}

.small {
    max-width: 100px;
    max-height: 100px;
}
</style>
