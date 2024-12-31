<template>
  <el-dialog
    :visible="visible"
    @close="handleClose"
    title="添加好友"
    width="400px"
    :before-close="handleClose"
    class="add-friend-dialog"
  >
    <el-input
      v-model="username"
      placeholder="请输入好友的用户名"
      class="input-field"
      size="large"
      clearable
    />
    
    <!-- 添加附言输入框 -->
    <el-input
      v-model="message"
      type="textarea"
      placeholder="输入附言（可选）"
      :rows="4"
      class="input-field"
      size="large"
    />

    <div slot="footer" class="dialog-footer">
      <el-button @click="handleClose" class="footer-btn">取消</el-button>
      <el-button type="primary" @click="sendFriendRequest" class="footer-btn">添加</el-button>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, defineProps, defineEmits, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { addFriendRequest } from '@/api/friendRequest';  // 发送好友请求的API
import { getUserByUsername } from '@/api/user';           // 根据用户名获取用户信息的API

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
const username = ref('');  // 用户名输入框
const message = ref('');   // 附言输入框

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
    // 通过用户名获取目标用户信息
    const friend = await getUserByUsername(username.value);
    if (friend) {
      // 发送好友请求，并附带附言消息
      console.log(props.userId,friend.id, message.value)
      await addFriendRequest(props.userId, friend.id, message.value);  // 发送请求，包括附言
      emit('friendAdded');  // 通知父组件请求已添加
      emit('update:visible', false);  // 关闭对话框
    } else {
      ElMessage.error('没有找到该用户');
    }
  } catch (error) {
    console.error('添加好友失败:', error);
  }
};
</script>

<style scoped>
.add-friend-dialog .el-dialog__header {
  background-color: #f8f8f8;
  border-bottom: 1px solid #e4e7ed;
  color: #333;
}

.el-dialog__header .el-dialog__title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.el-dialog__body {
  padding: 20px;
}

.input-field {
  margin-bottom: 15px;
  border-radius: 10px;
}

.footer-btn {
  border-radius: 20px;
  width: 100px;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
}

.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
  border-radius: 20px;
}

.el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.el-button--default {
  background-color: #f5f7fa;
  border-color: #dcdfe6;
  border-radius: 20px;
}

.el-button--default:hover {
  background-color: #e4e7ed;
}

.el-input {
  border-radius: 10px;
}

.el-input__inner {
  padding: 12px;
  font-size: 14px;
  color: #333;
}

.el-dialog {
  border-radius: 15px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}
</style>
