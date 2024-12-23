<template>
    <div style="border: 1px solid #ccc">
        <Toolbar style="border-bottom: 1px solid #ccc" :editor="editorRef" :defaultConfig="toolbarConfig"
            :mode="mode" />
        <Editor class="editor-container" ref="editor" style="height: 500px;" v-model="content"
            :defaultConfig="editorConfig" :mode="mode" @onCreated="handleCreated" />
    </div>
</template>

<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css'; // 引入 css
import { onBeforeUnmount, ref, shallowRef, watch, defineExpose } from 'vue';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import type { IDomEditor } from '@wangeditor/editor';

// 接收父组件传入的值
const props = defineProps({
    modelValue: {
        type: String,
        required: true
    },
    mode: {
        type: String,
        default: 'default' // 添加 mode 属性的默认值
    }
});

const emits = defineEmits(['update:modelValue']);

const editorRef = shallowRef<IDomEditor | null>(null);
const content = ref(props.modelValue);

const toolbarConfig = {};
const editorConfig = { placeholder: '请输入内容...' };

// 记录编辑器实例
const handleCreated = (editor: IDomEditor) => {
    editorRef.value = editor;
};

// 设置内容
function setContent(content: string) {
    editorRef.value?.setHtml(content);
}

// 清除内容
function clearContent() {
    editorRef.value?.setHtml(''); // 清除编辑器内容
}

// 同步编辑器内容与父组件的 v-model
watch(content, (newValue) => {
    emits('update:modelValue', newValue);
});

// 组件销毁时，及时销毁编辑器实例
onBeforeUnmount(() => {
    const editor = editorRef.value;
    if (editor) {
        editor.destroy();
    }
});


defineExpose({
    setContent,
    clearContent
})
</script>

<style scoped>
.w-e-text-container,
.w-e-text {
    white-space: pre-wrap;
    word-wrap: break-word;
    word-break: break-word;
}
</style>
