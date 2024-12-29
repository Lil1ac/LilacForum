<template>
  <el-dialog :visible="visible" @close="handleClose" title="添加好友">
    <el-input v-model="username" placeholder="请输入好友的用户名" />
    <span slot="footer" class="dialog-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="sendFriendRequest">添加</el-button>
    </span>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { addFriendRequest } from '@/api/friendship';
import { getUserByUsername } from '@/api/user';
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  userId: {
    type: Number,
    required: true
  }
});

const emit = defineEmits<{
  (e: 'friendAdded'): void;
  (e: 'update:visible', visible: boolean): void;
}>();

const visible = ref(props.visible);  // 控制本地显示状态
const username = ref('');

// 监听 props.visible 的变化，更新本地 visible 状态
watch(() => props.visible, (newVal) => {
  visible.value = newVal;
});

// 关闭对话框
const handleClose = () => {
  emit('update:visible', false); // 通知父组件关闭弹窗
};

// 发送好友请求
const sendFriendRequest = async () => {
  if (!username.value.trim()) {
    ElMessage.error('请输入有效的用户名');
    return;
  }
  try {
    // 假设这里是通过用户名获取目标用户ID的函数
    const friend = await getUserByUsername(username.value);
    if (friend) {
      await addFriendRequest(props.userId, friend.id);  // 发送请求
      emit('friendAdded');  // 通知父组件请求已添加
      emit('update:visible', false);  // 关闭对话框
    } else {
      ElMessage.error('没有找到该用户');
    }
  } catch (error) {
    ElMessage.error('添加好友失败，请重试');
    console.error('添加好友失败:', error);
  }
};


</script>

<style scoped>
/* 样式调整 */
</style>
