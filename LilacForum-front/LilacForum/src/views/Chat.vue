<template>
  <div class="chat-container">
    <el-row>
      <el-col :span="8">
        <el-card class="card-container">
          <div class="card-header">
            在线用户
            <span class="sub-text">（点击聊天气泡开始聊天）</span>
          </div>

          <!-- 管理员列表 -->
          <div class="user-list">
            <div class="list-title">管理员</div>
            <div v-for="user in adminUsers" :key="user.username" class="user-item">
              <span>{{ user.username }}</span>
              <el-icon class="el-icon-chat-dot-round icon" @click="selectToUser(user)">
                <ChatDotRound />
              </el-icon>
              <span class="chat-status" v-if="user.id === chatUser.id">chatting...</span>
            </div>
          </div>

          <!-- 普通用户列表 -->
          <div class="user-list">
            <div class="list-title">普通用户</div>
            <div v-for="user in normalUsers" :key="user.username" class="user-item">
              <span>{{ user.username }}</span>
              <el-icon class="el-icon-chat-dot-round icon" @click="selectToUser(user)">
                <ChatDotRound />
              </el-icon>
              <span class="chat-status" v-if="user.id === chatUser.id">chatting...</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧聊天区域 -->
      <el-col :span="16">
        <div class="chat-box">
          <div class="chat-header">
            Web聊天室（{{ chatUser.username }}）
          </div>
          <div id="message-container" class="message-container">
            <div v-for="item in messages" :key="item.id" class="message-box">
              <!-- 自己 -->
              <div v-if="item.fromUserId === currentUser.id" class="message right">
                <img :src="item.fromAvatar" alt="头像" class="avatar" />
                <div v-if="item.type === 'text'" class="message-content" v-html="item.content"></div>
                <div v-if="item.type === 'img'" class="message-content">
                  <el-image :src="item.content" alt="图片" :preview-src-list="[item.content]" @load="scrollToBottom" />
                </div>
                <div v-if="item.type === 'file'" class="message-content download" @click="download(item.content)">
                  <i class="el-icon-folder-opened"></i>
                  <span>{{ item.content.substring(item.content.indexOf('-') + 1) }}</span>
                </div>
              </div>
              <!-- 对象 -->
              <div v-else class="message left">
                <img :src="item.fromAvatar" alt="头像" class="avatar" />
                <div v-if="item.type === 'text'" class="message-content" v-html="item.content"></div>
                <div v-if="item.type === 'img'" class="message-content">
                  <el-image :src="item.content" alt="图片" :preview-src-list="[item.content]" @load="scrollToBottom" />
                </div>
                <div v-if="item.type === 'file'" class="message-content download" @click="download(item.content)">
                  <i class="el-icon-folder-opened"></i>
                  <span>{{ item.content.substring(item.content.indexOf('-') + 1) }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="chat-footer">
            <!-- 表情面板 -->
            <el-popover placement="top" width="200" trigger="click">
              <template #default>
                <div class="emoji-box">
                  <span v-for="(item, index) in emojis" :key="index" class="emoji" v-html="item"
                    @click="clickEmoji(item)"></span>
                </div>
              </template>
              <template #reference>
                <el-icon class="emoji-btn" icon="el-icon-smile" circle>
                  <PictureFilled />
                </el-icon>
              </template>
            </el-popover>

            <!-- 文件上传按钮的外部容器 -->

            <el-upload type="primary" class="upload-button" :http-request="customUpload" :show-file-list="false">
              <el-icon>
                <Upload />
              </el-icon>
            </el-upload>


            <!-- 富文本编辑区域 -->
            <div id="im-content" contenteditable="true" class="content-editable" placeholder="请输入消息..."></div>

            <!-- 发送按钮 -->
            <el-button type="primary" @click="send" class="send-button">发送</el-button>
          </div>

        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount } from 'vue';
import { getUsersByRole } from '@/api/user'; // 引入 API
import type { User } from '@/interface/User';
import type { Message } from '@/interface/Message';
import { fetchMessages,  sendMessage, setUnReadNums, loadUnReadNums } from '@/api/message';
import { uploadImage } from '@/api/upload';
import { ElMessage } from 'element-plus';
import { getUserInfo } from '@/api/user';


const circleUrl = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';



export default defineComponent({
  name: 'Chat',
  setup() {
    const currentUser = ref<User>({
      id: 0,
      username: '',
      password: '',
      email: '',
      gender: '',
      age: 0,
      profession: '',
      hobby: '',
      bio: '',
      avatar: circleUrl, // 你可以用一个默认头像 URL
      role: 'guest',
    });
    const chatUser = ref<User>({
      id: 0,
      username: '',
      password: '',
      email: '',
      gender: '',
      age: 0,
      profession: '',
      hobby: '',
      bio: '',
      avatar: circleUrl, // 你可以用一个默认头像 URL
      role: 'guest',
    });
    const adminUsers = ref<User[]>([]);
    const normalUsers = ref<User[]>([]);

    const text = ref<string>('');
    const messages = ref<Message[]>([]);
    const socket = ref<WebSocket | null>(null);
    const toAvatar = ref(''); // 选择的用户头像
    const unRead = ref<number>(0); // 未读消息数量
    const emojis = ref<string[]>(['😊', '😂', '😍', '😎', '😜']);




    // 获取用户列表
    const fetchUsers = async () => {
      try {
        const userData = localStorage.getItem('user');
        if (userData) {
          const user = JSON.parse(userData);
          if (user.userId) {
            user.id = user.userId;  // 将 userId 映射为 id
            delete user.userId;  // 删除原始的 userId 字段
          }
          currentUser.value = user; // 如果获取到的用户数据有效，再进行赋值
          const currentUserKey = `${currentUser.value.role}_${currentUser.value.username}`;
          adminUsers.value = await getUsersByRole('ADMIN', currentUserKey);
          normalUsers.value = await getUsersByRole('USER', currentUserKey);
        }
      } catch (error) {
        console.error('获取用户列表失败:', error);
      }
    };



    // 建立 WebSocket 连接
    const connectSocket = () => {

      const socketUrl = `ws://localhost:8080/chat/` + currentUser.value?.username; // WebSocket 服务端 URL
      socket.value = new WebSocket(socketUrl);

      socket.value.onopen = () => {
        console.log('WebSocket连接成功');
      };

      socket.value.onmessage = (msg) => {
        if (msg.data) {
          const message: Message = JSON.parse(msg.data);
          if (message.content && message.toUserId === currentUser.value.id) {
            messages.value.push(message);
            scrollToBottom(); // 新消息滚动到最底部
          }
          //加载消息数字
          if (chatUser.value.id === message.fromUserId) {
            setUnReadNums(currentUser.value.id, chatUser.value.id);
          } else {
            loadUnReadNums(currentUser.value.id); // 更新未读消息数
          }

        }

      };

      socket.value.onclose = () => {
        console.log('WebSocket连接关闭');
      };

      socket.value.onerror = (error) => {
        console.error('WebSocket连接错误:', error);
      };
    };

    // 表情点击事件
    const clickEmoji = (emoji: string) => {
      const contentEditable = document.getElementById('im-content');
      if (contentEditable) {
        contentEditable.innerHTML += emoji;  // 添加表情到编辑框
      }
    };

    const customUpload = async (options: any) => {
      const { file, onSuccess, onError } = options; // el-upload 提供的选项
      try {
        const url = await uploadImage(file);
        onSuccess(url); // 告知 el-upload 上传成功
        ElMessage.success('图片上传成功');
      } catch (error) {
        onError(error); // 告知 el-upload 上传失败
      }
    };



    // 文件上传成功的回调
    const handleFile = (response: any, file: File) => {
      const contentEditable = document.getElementById('im-content');
      if (contentEditable) {
        contentEditable.innerHTML += `<a href="${response.url}" target="_blank">${file.name}</a>`;  // 显示文件链接
      }
    };


    //发送消息
    const send = () => {
      const contentEditable = document.getElementById('im-content') as HTMLElement;
      console.log(contentEditable);

      // 确保选择了聊天对象
      if (!chatUser.value) {
        console.log('请选择聊天对象');
        return;
      }

      // 确保消息内容不为空
      const messageContent = contentEditable ? contentEditable.innerHTML.trim() : '';
      if (!messageContent) {
        console.log('请输入内容');
        return;
      }

      // 获取当前时间（假设你需要传送消息的时间）
      const currentTime = new Date().toISOString();  // 使用 ISO 8601 格式

      // 创建消息对象
      const message: Message = {
        id: Date.now(),  // 使用时间戳作为唯一的消息 ID
        content: messageContent,  // 消息内容
        fromUserId: currentUser.value?.id || 0,  // 发送者用户名
        fromAvatar: currentUser.value?.avatar || circleUrl,  // 发送者头像
        time: currentTime,  // 消息时间
        type: 'text',  // 消息类型，这里假设是文本类型
        toUserId: chatUser.value.id,  // 接收者用户名
        toAvatar: toAvatar.value || '',  // 接收者头像
        isRead: 0  // 初始为未读
      };

      console.log('发送消息:', message);

      // 发送消息
      if (socket.value) {
        socket.value.send(JSON.stringify(message));  // 通过 WebSocket 发送消息
        messages.value.push(message);  // 将消息添加到消息列表
        contentEditable.innerHTML = '';  // 清空文本框内容
      }


    };


    // 滚动到底部
    const scrollToBottom = () => {
      const container = document.getElementById('message-container');
      if (container) {
        container.scrollTop = container.scrollHeight;
      }
    };



    // // 获取消息历史
    // const fetchMessages = async () => {
    //   try {
    //     const response = await axios.get('/api/messages'); // 获取消息的接口
    //     messages.value = response.data;
    //   } catch (error) {
    //     console.error('获取消息列表失败:', error);
    //   }
    // };

    // 选择聊天对象
    const selectToUser = async (user: User) => {
      try {

        // 调用获取用户信息的方法
        const targetUser = await getUserInfo(user.id);  // 假设 item 中包含 userId

        // 设置当前聊天对象
        chatUser.value = targetUser;

        // 设置聊天对象的用户信息
        chatUser.value.username = user.role + user.username;  // 选择用户时拼接用户名和角色
        toAvatar.value = targetUser.avatar;  // 获取用户头像


        // 如果需要加载其他内容，可以调用 load 方法
        loadMessages(currentUser.value.id, chatUser.value.id);

      } catch (error) {
        console.error('获取用户信息失败:', error);
      }
    };

    // 下载文件
    const download = (file: string) => {
      window.open(file);
    };


    // 加载历史消息
    const loadMessages = async (currentUserId: number, chatUserId: number) => {
      try {
        console.log('加载历史消息:', currentUserId, chatUserId);
        const result = await fetchMessages(currentUserId, chatUserId);
        messages.value = result;
      } catch (error) {
        console.error('加载消息失败:', error);
      }
    };





    onMounted(() => {
      fetchUsers();
      connectSocket();
    });

    onBeforeUnmount(() => {
      if (socket.value) {
        socket.value.close();
      }
    });

    return {
      currentUser,
      adminUsers,
      normalUsers,
      chatUser,
      text,
      messages,
      toAvatar,
      unRead,
      emojis,
      send,
      scrollToBottom,
      selectToUser,
      download,
      loadUnReadNums,
      clickEmoji,
      handleFile,
      customUpload,
    };
  }
});
</script>

<style scoped>
.chat-container {
  padding: 20px;
  margin-bottom: 50px;
}

.card-container {
  width: 100%;
  min-height: 300px;
  color: #333;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  /* 卡片阴影 */
  border-radius: 10px;
}

.card-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #ccc;
  font-size: 16px;
  font-weight: bold;
}

.sub-text {
  font-size: 12px;
  color: #888;
}

.user-list {
  padding: 10px 0;
}

.list-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #555;
}

.user-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  padding: 8px;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.user-item:hover {
  background-color: #f0f0f0;
  /* 鼠标悬停时的背景色 */
}

/* 用户头像 */
.user-item img {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

/* 用户状态 */
.chat-status {
  font-size: 12px;
  color: #666;
  margin-left: 5px;
}


.chat-status {
  font-size: 12px;
  color: limegreen;
  margin-left: 5px;
}

.chat-box {
  width: 800px;
  margin: 0 auto;
  background-color: white;
  border-radius: 5px;
  box-shadow: 0 0 10px #ccc;
}

.chat-header {
  text-align: center;
  line-height: 50px;
}

.message-container {
  height: 350px;
  overflow: auto;
  border-top: 1px solid #ccc;
}

.message-box {
  display: flex;
  flex-direction: column;
  margin: 10px 0;
}

.message {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.right {
  justify-content: flex-end;
}

.left {
  justify-content: flex-start;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  margin-right: 10px;
}

.message-content {
  max-width: 60%;
  word-wrap: break-word;
  white-space: pre-wrap;
}

.download {
  cursor: pointer;
  color: #409eff;
}

.chat-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background-color: #f1f1f1;
  border-top: 1px solid #ddd;
  position: relative;
}

.emoji-btn {
  margin-right: 15px;
  padding: 5px;
  cursor: pointer;
}

.emoji-box {
  display: flex;
  flex-wrap: wrap;
  min-width: 150px;
}

.emoji {
  margin: 5px;
  font-size: 20px;
  cursor: pointer;
}

.upload-container {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  cursor: pointer;
}

.upload-container:hover {
  background-color: #f7f7f7;
}

.upload-button {
  display: inline-block;
  margin-right: 15px;
  cursor: pointer;
}

.upload-icon {
  font-size: 24px;
}

.content-editable {
  flex: 1;
  min-width: 200px;
  padding: 10px;
  background-color: #fff;
  border-radius: 5px;
  border: 1px solid #ccc;
  min-height: 40px;
  box-sizing: border-box;
  outline: none;
  font-size: 14px;
}

.content-editable:empty:before {
  content: attr(placeholder);
  color: #888;
}

.send-button {
  margin-left: 15px;
}
</style>