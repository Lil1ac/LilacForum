<template>
  <div style="padding: 10px; margin-bottom: 50px">
    <el-row>
      <el-col :span="8">
        <el-card style="width: 100%; min-height: 300px; color: #333">
          <div style="padding-bottom: 10px; border-bottom: 1px solid #ccc">
            在线用户
            <span style="font-size: 12px">（点击聊天气泡开始聊天）</span>
          </div>

          <!-- 管理员列表 -->
          <div style="padding: 10px 0">
            <div style="font-size: 14px; font-weight: bold; margin-bottom: 10px">管理员</div>
            <div v-for="user in adminUsers" :key="user.username">
              <span>{{ user.username }}</span>
              <el-icon class="el-icon-chat-dot-round" style="margin-left: 10px; font-size: 16px; cursor: pointer"
                @click="selectToUser(user)">
                <ChatDotRound />
              </el-icon>
              <span style="font-size: 12px; color: limegreen; margin-left: 5px"
                v-if="user.username === chatUser">chatting...</span>
            </div>
          </div>

          <!-- 普通用户列表 -->
          <div style="padding: 10px 0">
            <div style="font-size: 14px; font-weight: bold; margin-bottom: 10px">普通用户</div>
            <div v-for="user in normalUsers" :key="user.username">
              <span>{{ user.username }}</span>
              <el-icon class="el-icon-chat-dot-round" style="margin-left: 10px; font-size: 16px; cursor: pointer"
                @click="selectToUser(user)">
                <ChatDotRound />
              </el-icon>
              <span style="font-size: 12px; color: limegreen; margin-left: 5px"
                v-if="user.username === chatUser">chatting...</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧聊天区域 -->
      <el-col :span="16">
        <div
          style="width: 800px; margin: 0 auto; background-color: white; border-radius: 5px; box-shadow: 0 0 10px #ccc">
          <div style="text-align: center; line-height: 50px;">
            Web聊天室（{{ chatUser }}）
          </div>
          <div id="message-container" style="height: 350px; overflow: auto; border-top: 1px solid #ccc">
            <div v-for="item in messages" :key="item.id" class="message-box">
              <div v-if="item.fromUser === currentUser.username" class="message right">
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
          <div style="height: 200px">
            <!-- 输入区域 -->
            <div style="padding: 10px;">
              <!-- 表情面板 -->
              <el-popover placement="top" width="200" trigger="click">
                <div class="emoji-box">
                  <span v-for="(item, index) in emojis" :key="index" style="font-size: 20px; cursor: pointer;"
                    v-html="item" @click="clickEmoji(item)">
                  </span>
                </div>
                <i slot="reference" class="fa fa-smile-o" style="font-size: 20px; cursor: pointer;"></i>
              </el-popover>

              <!-- 文件上传 -->
              <el-upload action="http://localhost:8080/files/upload" :show-file-list="false" :on-success="handleFile">
                <i class="fa fa-folder-open-o" style="font-size: 20px; cursor: pointer;"></i>
              </el-upload>

              <!-- 富文本编辑区 -->
              <div id="im-content" contenteditable="true"
                style="border: 1px solid #ccc; padding: 10px; min-height: 80px; margin-top: 10px; border-radius: 5px;">
              </div>

              <!-- 发送按钮 -->
              <el-button type="primary" @click="send" style="width: 100%; margin-top: 10px;">发送</el-button>
            </div>
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
import { fetchMessages, fetchUnreadNums, sendMessage } from '@/api/message';

const circleUrl = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';



export default defineComponent({
  name: 'Chat',
  setup() {
    const currentUser = ref < User > ({
      uid: 0,
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

    const adminUsers = ref < User[] > ([]);
    const normalUsers = ref < User[] > ([]);
    const chatUser = ref < string > ('');
    const text = ref < string > ('');
    const messages = ref < Message[] > ([]);
    const socket = ref < WebSocket | null > (null);
    const touser = ref(''); // 选择的用户
    const toAvatar = ref(''); // 选择的用户头像
    const unRead = ref < number > (0); // 未读消息数量
    const emojis = ref < string[] > (['😊', '😂', '😍', '😎', '😜']);
    // 获取用户列表
    const fetchUsers = async () => {
      try {
        const userData = localStorage.getItem('user');
        if (userData) {
          currentUser.value = JSON.parse(userData); // 如果获取到的用户数据有效，再进行赋值
          const role = currentUser.value.role;
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

      socket.value.onmessage = (event) => {
        const message: Message = JSON.parse(event.data);
        messages.value.push(message);
        scrollToBottom(); // 新消息滚动到最底部
        loadUnReadNums(); // 更新未读消息数
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

    // 文件上传成功的回调
    const handleFile = (response: any, file: File) => {
      const contentEditable = document.getElementById('im-content');
      if (contentEditable) {
        contentEditable.innerHTML += `<a href="${response.url}" target="_blank">${file.name}</a>`;  // 显示文件链接
      }
    };
    const send = () => {
      if (!chatUser.value) {
        console.log('请选择聊天对象');
        return;
      }
      if (!text.value) {
        console.log('请输入内容');
        return;
      }

      // 获取当前时间（假设你需要传送消息的时间）
      const currentTime = new Date().toISOString();  // 使用 ISO 8601 格式
      const contentEditable = document.getElementById('im-content');
      if (contentEditable && contentEditable.innerHTML) {
        // 创建消息对象
        const message: Message = {
          id: Date.now(),  // 使用时间戳作为唯一的消息 ID
          content: text.value,  // 消息内容
          fromUser: currentUser.value?.username || '',  // 发送者用户名
          fromAvatar: currentUser.value?.avatar || circleUrl,  // 发送者头像
          time: currentTime,  // 消息时间
          type: 'text',  // 消息类型，这里假设是文本类型
          toUser: chatUser.value,  // 接收者用户名
          toAvatar: toAvatar.value || '',  // 接收者头像
          isRead: 0  // 初始为未读
        };
        // 发送消息
        if (socket.value) {
          socket.value.send(JSON.stringify(message));  // 通过 WebSocket 发送消息
          messages.value.push(message);  // 将消息添加到消息列表
          text.value = '';  // 清空文本框内容
        }
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

    // 选择聊天用户
    const selectToUser = (item: User) => {
      touser.value = item.role + item.username; // 选择用户时拼接用户名和角色
      toAvatar.value = item.avatar; // 获取用户头像
      chatUser.value = item.username; // 设置当前聊天用户
      loadMessages(currentUser.value.username); // 如果需要加载其他内容，可以调用 load 方法
    };

    // 下载文件
    const download = (file: string) => {
      window.open(file);
    };


    // 加载历史消息
    const loadMessages = async (chatUser: string) => {
      try {
        const result = await fetchMessages(currentUser.value.username, chatUser);
        messages.value = result;
      } catch (error) {
        console.error('加载消息失败:', error);
      }
    };


    // 获取未读消息数
    const loadUnReadNums = async () => {
      try {
        const result = await fetchUnreadNums(currentUser.value.username);
        unRead.value = result;
      } catch (error) {
        console.error('获取未读消息数失败:', error);
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
      touser,
      toAvatar,
      unRead,
      emojis,
      send,
      scrollToBottom,
      selectToUser,
      download,
      loadUnReadNums,
      clickEmoji,
      handleFile
    };
  }
});
</script>

<style scoped>
.message {
  display: flex;
  align-items: center;
  margin: 10px;
}

.message.left {
  justify-content: flex-start;
}

.message.right {
  justify-content: flex-end;
}

.avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
}

.message-content {
  max-width: 70%;
  word-wrap: break-word;
  background-color: #f3f3f3;
  padding: 10px;
  border-radius: 5px;
  margin-left: 10px;
  margin-right: 10px;
}

.download {
  cursor: pointer;
  font-size: 14px;
  color: #409eff;
}
</style>
