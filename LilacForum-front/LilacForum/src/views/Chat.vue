<template>
  <div class="chat-container">
    <el-card class="card-container">
      <div class="card-header">
        在线用户
        <span class="sub-text">（点击聊天气泡开始聊天）</span>
      </div>
      <div class="user-list scroll-container">
        <!-- 管理员列表 -->
        <div class="admin-list">
          <div class="list-title">管理员</div>
          <div v-for="user in adminUsers" :key="user.username" class="user-item" @click="selectToUser(user)"
            :class="{ 'active': selectedUserId === user.id }">
            <div class="user-left">
              <img :src="user.avatar || defaultAvatar" alt="头像" class="user-avatar" />
              <el-badge v-if="user.isOnline" class="online-badge" :dot="true"></el-badge>
            </div>
            <div class="user-info">
              <div class="username">{{ user.username }}</div>
              <div class="last-message" v-if="user.lastMessage">
                <span class="last-message-text">
                  {{ user.lastMessage.content }}
                </span>
              </div>
            </div>
            <div v-if="user.lastMessage" class="user-time">
              <span>{{ formatTime(user.lastMessage.time) }}</span>
            </div>
            <div class="unread-bubble" v-if="unreadCounts[user.id] > 0">
              {{ unreadCounts[user.id] }}
            </div>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="divider"></div>

        <!-- 好友列表 -->
        <div class="myFriend-user-list">
          <div class="list-title">我的好友</div>
          <div v-for="user in myFriends" :key="user.username" class="user-item" @click="selectToUser(user)"
            :class="{ 'active': selectedUserId === user.id }">
            <div class="user-left">
              <img :src="user.avatar || defaultAvatar" alt="头像" class="user-avatar" />
              <el-badge v-if="user.isOnline" class="online-badge" :dot="true"></el-badge>
            </div>
            <div class="user-info">
              <div class="username">{{ user.username }}</div>
              <div class="last-message" v-if="user.lastMessage">
                <span class="last-message-text">
                  {{ user.lastMessage.content }}
                </span>
              </div>
            </div>
            <div v-if="user.lastMessage" class="user-time">
              <span>{{ formatTime(user.lastMessage.time) }}</span>
            </div>
            <div class="unread-bubble" v-if="unreadCounts[user.id] > 0">
              {{ unreadCounts[user.id] }}
            </div>
          </div>
        </div>

        <!-- 分隔线 -->
        <div class="divider"></div>

        <!-- 普通在线用户列表 -->
        <div class="normal-user-list">
          <div class="list-title">普通用户</div>
          <div v-for="user in normalUsers" :key="user.username" class="user-item" @click="selectToUser(user)"
            :class="{ 'active': selectedUserId === user.id }">
            <!-- <el-dropdown trigger="contextmenu" @command="handleFriendMenuCommand(user)">
              <el-button style="display: none;"></el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="addFriend">添加好友</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown> -->
            <div class="user-left">
              <img :src="user.avatar || defaultAvatar" alt="头像" class="user-avatar" />
              <el-badge v-if="user.isOnline" class="online-badge" :dot="true"></el-badge>
            </div>
            <div class="user-info">
              <div class="username">{{ user.username }}</div>
              <div class="last-message" v-if="user.lastMessage">
                <span class="last-message-text">
                  {{ user.lastMessage.content }}
                </span>
              </div>
            </div>
            <div v-if="user.lastMessage" class="user-time">
              <span>{{ formatTime(user.lastMessage.time) }}</span>
            </div>
            <div class="unread-bubble" v-if="unreadCounts[user.id] > 0">
              {{ unreadCounts[user.id] }}
            </div>
          </div>
        </div>
      </div>


    </el-card>


    <!-- 右侧聊天区域 -->
    <div class="chat-box">
      <div class="chat-header">
        {{ chatUser?.username || '点击用户开始聊天' }}
        <span v-if="chatUser && chatUser.isOnline" class="status online">在线</span>
        <span v-if="chatUser && !chatUser.isOnline" class="status offline">离线</span>
      </div>

      <div id="message-container" class="message-container scroll-container" @scroll="onScroll">
        <div v-for="(item, index) in messages" :key="item.id" class="message-box">
          <!-- 显示时间 -->
          <div v-if="shouldShowTime(item, index)" class="message-time">
            {{ formatTime(item.time) }}
          </div>
          <!-- 自己 -->
          <div v-if="item.fromUserId === currentUser.id" class="message right">
            <!-- 文本消息 -->
            <div v-if="item.type === 'text'" class="message-content" v-html="item.content"></div>
            <!-- 图片消息 -->
            <div v-if="item.type === 'img'" class="message-image">
              <el-image class="message-el-image" :src="item.content" alt="图片" :preview-src-list="[item.content]"
                fit="cover" />
            </div>
            <!-- 文件消息 -->
            <div v-if="item.type === 'file'" class="message-file download" @click="download(item.content)">
              <i class="el-icon-folder-opened"></i>
              <span>{{ item.content.substring(item.content.indexOf('-') + 1) }}</span>
              <div class="file-hint">点击下载</div>
            </div>
            <img :src="item.fromAvatar" alt="头像" class="avatar" />

          </div>
          <!-- 对象 -->
          <div v-else class="message left">
            <img :src="item.fromAvatar" alt="头像" class="avatar" />
            <!-- 文本消息 -->
            <div v-if="item.type === 'text'" class="message-content" v-html="item.content"></div>
            <!-- 图片消息 -->
            <div v-if="item.type === 'img'" class="message-image">
              <el-image class="message-el-image" :src="item.content" alt="图片" :preview-src-list="[item.content]"
                fit="cover" />
            </div>
            <!-- 文件消息 -->
            <div v-if="item.type === 'file'" class="message-file download" @click="download(item.content)">
              <i class="el-icon-folder-opened"></i>
              <span>{{ item.content.substring(item.content.indexOf('-') + 1) }}</span>
              <div class="file-hint">点击下载</div>
            </div>

          </div>
        </div>
        <!-- 当前对话新消息提示 -->
        <div v-if="newMessage" class="new-message-alert" @click="clearNewMessage">
          有新消息
        </div>
      </div>

      <div class="chat-footer">
        <!-- 表情面板 -->
        <el-popover placement="top" width="400" trigger="click">
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
  </div>
</template>


<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount } from 'vue';
import { getUsersByRole } from '@/api/user'; // 引入 API
import type { User } from '@/interface/User';
import type { Message } from '@/interface/Message';
import type { WSMessage } from '@/interface/WSMessage';
import { fetchMessages, sendMessage, getUnReadNum, fetchLastMessage } from '@/api/message';
import { uploadImage } from '@/api/upload';
import { ElMessage } from 'element-plus';
import { getUserInfo } from '@/api/user';
import { emojis } from '@/assets/emoji';
import format from '@/utils/format';
import { nextTick } from 'vue';
import { getFriends } from '@/api/friendship';
import { addFriendRequest } from '@/api/friendRequest';
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';



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
      avatar: defaultAvatar,
      role: 'guest',
      isOnline: false,
    });
    const chatUser = ref<User>();
    const adminUsers = ref<User[]>([]);
    const myFriends = ref<User[]>([]);
    const normalUsers = ref<User[]>([]);
    const finalUsers = ref<User[]>([]); // 最终去重后的用户列表


    const text = ref<string>('');
    const messages = ref<Message[]>([]);
    const socket = ref<WebSocket | null>(null);
    const toAvatar = ref(''); // 选择的用户头像
    const unRead = ref<number>(0); // 未读消息数量
    const unreadCounts = ref<{ [key: number]: number }>({});  // 用一个对象来存储每个用户的未读消息数，key 是用户ID，value 是未读消息数
    const newMessage = ref<boolean>(false); // 是否当前对话有新消息

    // 获取用户列表并加载每个用户的最后一条消息
    const fetchUsers = async () => {
      const userData = localStorage.getItem('user');
      if (!userData) return;
      const user = JSON.parse(userData);
      if (user.userId) {
        user.id = user.userId;  // 将 userId 映射为 id
        delete user.userId;  // 删除原始的 userId 字段
      }
      currentUser.value = user; // 如果获取到的用户数据有效，再进行赋值
      const currentUserKey = `${currentUser.value.username}`;

      // 获取管理员列表
      adminUsers.value = await getUsersByRole('ADMIN', currentUserKey);
      // 获取好友列表
      myFriends.value = await getFriends(currentUser.value.id);
      // 将好友列表减去管理员列表，避免重复
      const uniqueFriends = myFriends.value.filter(friend =>
        !adminUsers.value.some(admin => admin.id === friend.id)
      );
      myFriends.value = uniqueFriends;
      // 获取普通用户列表
      normalUsers.value = await getUsersByRole('USER', currentUserKey);
      // 将普通用户列表减去好友列表，避免重复
      const uniqueNormalUsers = normalUsers.value.filter(user =>
        !myFriends.value.some(friend => friend.id === user.id)
      );

      // 合并三类用户：管理员、好友（排除重复）、普通用户（排除重复）
      finalUsers.value = [...adminUsers.value, ...uniqueFriends, ...uniqueNormalUsers];
      normalUsers.value = uniqueNormalUsers;
      // 更新每个用户的最后一条消息和状态
      await fetchAndSetLastMessagesAndStatus(finalUsers.value, currentUser.value);

      loadUnReadNum(currentUser.value.id);

      sortAllUsers();
    };

    // 按照最后一条消息的时间排序所有用户
    const sortAllUsers = () => {
      sortUsersByLastReplyTime(adminUsers.value);
      sortUsersByLastReplyTime(myFriends.value);
      sortUsersByLastReplyTime(normalUsers.value);
    };

    // 排序用户，按照最后一条消息的时间进行排序
    const sortUsersByLastReplyTime = (users: User[]) => {
      users.sort((a, b) => {
        const timeA = a.lastMessage ? new Date(a.lastMessage.time).getTime() : 0;
        const timeB = b.lastMessage ? new Date(b.lastMessage.time).getTime() : 0;
        return timeB - timeA; // 时间降序排列
      });
    };
    // 获取并设置每个用户的最后一条消息
    const fetchAndSetLastMessagesAndStatus = async (users: User[], currentUser: User) => {
      for (let user of users) {
        const lastMessage = await fetchLastMessage(currentUser.id, user.id);
        if (lastMessage) {
          user.lastMessage = lastMessage;
        }
      }
      if (socket.value) {
        // 连接成功后请求当前在线用户的状态
        const requestMessage = {
          type: 'getOnlineStatus',  // 请求类型
          data: null  // 可以根据需求添加附加数据（如果有）
        };
        // 发送请求获取在线状态信息
        socket.value.send(JSON.stringify(requestMessage));
      }
    };


    // 建立 WebSocket 连接
    const connectSocket = () => {
      const socketUrl = `${import.meta.env.VITE_SOCKET_URL}/chat/${currentUser.value?.username}`;
      console.log('socketUrl:', socketUrl);
      socket.value = new WebSocket(socketUrl);

      socket.value.onopen = () => {
        if (!socket.value) return;
        console.log('WebSocket连接成功');

      };

      socket.value.onmessage = (msg) => {
        if (msg.data) {
          const message: WSMessage = JSON.parse(msg.data);
          console.log('WebSocket收到消息:', message);
          if (!message) return;
          // 判断消息类型是否为在线状态更新
          if (message.type === 'status') {
            // 处理在线状态更新
            handleOnlineStatusUpdate(message.data);
          } else {
            // 处理其他消息（文本、图片等）
            handleChatMessage(message.data);
            if (chatUser.value) { //如果正在聊天        
              // 更新未读消息数
              if (chatUser.value.id === message.data.fromUserId) {
                // 是当前聊天，重置未读消息数
                setUnReadNum(currentUser.value.id, chatUser.value.id);
              } else {
                // 非当前聊天，更新未读消息数
                loadUnReadNum(currentUser.value.id);
              }
            }
            // 更新刚刚发送消息用户的最后一条消息
            updateLastMessage(message.data);
            sortAllUsers();
          }
        }
        nextTick(() => {
          scrollToBottom(); // 新消息滚动到最底部
        });
      };

      socket.value.onclose = () => {
        console.log('WebSocket连接关闭');
      };

      socket.value.onerror = (error) => {
        console.error('WebSocket连接错误:', error);
      };
    };

    // 处理在线状态更新
    const handleOnlineStatusUpdate = (userStatus: { [key: string]: boolean }) => {
      // 遍历用户状态
      Object.keys(userStatus).forEach((username) => {
        const isOnline = userStatus[username];
        const user = finalUsers.value.find(u => u.username === username);
        if (!user) return;
        if (isOnline) {
          user.isOnline = true;// 用户上线，添加到在线用户列表
        } else {
          user.isOnline = false; // 用户下线，从在线用户列表移除
        }
      })
    };



    // 处理其他消息（文本、图片等）
    const handleChatMessage = async (message: Message) => {
      if (!chatUser.value) return;
      if (message.fromUserId === currentUser.value.id && message.toUserId === chatUser.value.id) {
        // 自己发送的消息
        await messages.value.push(message);
        nextTick(() => {
          scrollToBottom(); // 新消息滚动到最底部
        });
      } else if (message.fromUserId === chatUser.value.id && message.toUserId === currentUser.value.id) {
        // 对方发送的消息
        messages.value.push(message);
        newMessage.value = true;
      }
    };

    // 更新特定用户的最后一条消息
    const updateLastMessage = (message: Message) => {
      // 获取所有的用户，包括管理员和普通用户
      const users = [...finalUsers.value];

      // 更新发送者的最后一条消息
      const sender = users.find(u => u.id === message.fromUserId);
      if (sender) {
        sender.lastMessage = { ...message }; // 创建新副本，避免修改原对象

      }

      // 更新接收者的最后一条消息
      const receiver = users.find(u => u.id === message.toUserId);
      if (receiver) {
        receiver.lastMessage = { ...message }; // 创建新副本，避免修改原对象

      }
    };


    // 表情点击事件
    const clickEmoji = (emoji: string) => {
      const contentEditable = document.getElementById('im-content');
      if (contentEditable) {
        contentEditable.innerHTML += emoji;  // 添加表情到编辑框
      }
    };

    // 文件上传
    const customUpload = async (options: any) => {
      const { file, onSuccess, onError } = options;
      try {
        const url = await uploadImage(file);
        await onSuccess(url);
        handleFile(url);
      } catch (error) {
        onError(error);
      }
    };

    // 文件上传成功的回调
    const handleFile = (url: string) => {
      if (!socket.value) {
        ElMessage.error('WebSocket not connected');
        return;
      }

      // 获取图片消息模板
      const message = getMessage('img');
      if (!message) return;

      message.content = url; // 使用上传后的 URL 作为消息内容
      const extName = url.split('.').pop();
      const validImageExtensions = ['png', 'jpg', 'jpeg', 'gif', 'bmp', 'tiff', 'svg', 'webp'];

      // 判断文件类型
      if (validImageExtensions.includes(extName || '')) {
        message.type = 'img'; // 标记为图片类型
      } else {
        message.type = 'file'; // 如果是其他类型，标记为文件类型
      }
      // 包装外层类型
      const wsMessage = {
        type: 'chat', // 外层包裹 type 字段，表示这是一个文件上传消息
        data: message // 内部包含实际的消息内容
      };
      console.log('wrapmesage', wsMessage)
      // 发送消息
      socket.value.send(JSON.stringify(wsMessage));
    };


    // 发送消息
    const send = () => {
      // 确保选择了聊天对象
      if (!chatUser.value || chatUser.value.id === 0) {
        ElMessage.error('请选择聊天对象');
        return;
      }

      // 获取消息对象
      const message = getMessage('text');
      if (!message) return;

      // 包装消息为符合 WebSocket 后端接口要求的结构
      const wsMessage = {
        type: 'chat',  // 消息类型（这里是文本消息）
        data: message  // 消息内容，data 字段包含实际的聊天消息
      };

      // 发送消息
      if (socket.value) {
        socket.value.send(JSON.stringify(wsMessage));  // 通过 WebSocket 发送消息
      }
    };





    //获取消息内容
    const getMessage = (type: string) => {
      if (!chatUser.value) return;
      // 确保消息内容不为空
      const contentEditable = document.getElementById('im-content') as HTMLElement;
      let messageContent = contentEditable ? contentEditable.innerHTML : '';
      messageContent = messageContent.replace(/<\/?[^>]+(>|$)/g, "");
      // 清除所有HTML标签，保留纯文本
      contentEditable.innerHTML = '';  // 清空文本框内容
      if (!messageContent && type === 'text') {
        ElMessage.error('消息内容不能为空');
        return;
      }
      // 获取当前时间
      const currentTime = new Date().toISOString();

      // 创建消息对象
      const message: Message = {
        id: Date.now(),
        content: messageContent,
        fromUserId: currentUser.value.id,
        fromUserName: currentUser.value.username,
        fromAvatar: currentUser.value.avatar,
        time: currentTime,
        type: type,
        toUserId: chatUser.value.id,
        toUserName: chatUser.value.username,
        toAvatar: chatUser.value.avatar,
        isRead: 0
      };

      contentEditable.innerHTML = '';
      return message;
    };



    // 滚动到底部
    const scrollToBottom = () => {
      const container = document.getElementById('message-container');
      if (container) {
        console.log('滚动到底部');
        container.scrollTop = container.scrollHeight;
      }
    };

    const isLoading = ref(false); // 标记是否正在加载，防抖
    const selectedUserId = ref<number | null>(null);
    // 选择聊天对象
    const selectToUser = async (user: User) => {
      if (isLoading.value) return;
      try {
        isLoading.value = true;
        // 设置当前聊天对象
        chatUser.value = user;
        selectedUserId.value = user.id;
        // 加载聊天记录
        await loadInitialMessages();
        scrollToBottom(); // 新消息滚动到最底部
      } catch (error) {
        console.error('获取用户信息失败:', error);
      } finally {
        isLoading.value = false; // 操作完成后，设置为加载完成状态
      };
    }

    // 下载文件
    const download = (file: string) => {
      window.open(file);
    };


    // 滚动事件处理
    const onScroll = (event: Event) => {
      const container = event.target as HTMLElement;
      // 滚动到了顶部，触发加载更多消息
      if (container.scrollTop <= 500) {
        loadMoreMessages();
      }
      // 如果滚动到底部，隐藏新消息提示
      const isAtBottom = container.scrollHeight - container.scrollTop === container.clientHeight;
      if (isAtBottom) {
        newMessage.value = false;
      }
    };



    const loading = ref(false);  // 用于防止重复加载
    // 初始化加载消息
    const loadInitialMessages = async () => {
      if (!chatUser.value) return;
      try {
        const result = await fetchMessages(currentUser.value.id, chatUser.value.id);
        messages.value = result;
        // 更新特定用户的未读消息数
        unreadCounts.value = await getUnReadNum(currentUser.value.id);  // 更新当前聊天用户的未读消息数
      } catch (error) {
        console.error('加载初始消息失败:', error);
      }
    };

    // 加载更多历史消息
    const loadMoreMessages = async () => {
      if (!chatUser.value) return;
      if (loading.value) return;
      loading.value = true;
      try {
        const lastMessage = messages.value[0];  // 获取最早的一条消息（顶部消息）
        const result = await fetchMessages(currentUser.value.id, chatUser.value.id, lastMessage ? lastMessage.id : 0);

        // 将加载的消息插入到现有消息的前面
        messages.value = [...result, ...messages.value]; // 将新加载的消息插入到顶部
      } catch (error) {
        ElMessage.error('加载更多消息失败');
      } finally {
        loading.value = false;
      }
    };
    // 设置未读消息数
    const setUnReadNum = async (currentUserId: number, chatUserId: number) => {
      await fetchMessages(currentUserId, chatUserId);
      loadUnReadNum(currentUserId);  // 更新当前聊天用户的未读消息数
    };

    //获取未读消息数
    const loadUnReadNum = async (currentUserId: number) => {
      const result = await getUnReadNum(currentUserId);
      unreadCounts.value = result;
    };


    // 判断是否显示时间
    const shouldShowTime = (item: Message, index: number): boolean => {
      if (index === 0) return true;
      const previousMessage = messages.value[index - 1];
      const currentMessageTime = new Date(item.time).getTime();
      const previousMessageTime = new Date(previousMessage.time).getTime();
      return Math.abs(currentMessageTime - previousMessageTime) > 60000 * 3; // 时间差大于3分钟，显示时间
    };

    //点击清除新消息
    const clearNewMessage = () => {
      newMessage.value = false;
      scrollToBottom();
    };

    // 右键菜单命令处理
    const handleFriendMenuCommand = async (user: any, command: string) => {
      if (command === 'addFriend') {
        try {
          // 调用API添加好友
          await addFriendRequest(currentUser.value.id, user.id);
          ElMessage.success(`成功将 ${user.username} 添加为好友`);
        } catch (error) {
          ElMessage.error('添加好友失败');
        }
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
      defaultAvatar,
      currentUser,
      adminUsers,
      myFriends,
      normalUsers,
      chatUser,
      text,
      messages,
      toAvatar,
      unRead,
      emojis,
      unreadCounts,
      newMessage,
      selectedUserId,
      send,
      scrollToBottom,
      selectToUser,
      download,
      loadUnReadNum,
      clickEmoji,
      handleFile,
      customUpload,
      shouldShowTime,
      formatTime: format.formatTime,
      onScroll,
      clearNewMessage,

    };
  }
});
</script>

<style scoped>
.chat-container {
  display: flex;
  padding: 20px;
  margin: 35px auto;
  max-width: 1200px;
}

.card-container {
  overflow: auto;
  height: 100%;
  border-radius: 0
}


.card-header {
  padding-bottom: 10px;
  border-bottom: 1px solid #ccc;
  font-size: 16px;
  font-weight: bold;
  border-radius: 0;
}

.sub-text {
  font-size: 12px;
  color: #888;
}

.user-list {
  padding: 10px 0;
  height: 660px;
  width: 250px;
  overflow-y: auto;
}

.list-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #555;
}

/* 横线分隔 */
.divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 10px 0;
}

.user-item {
  cursor: pointer;
  padding: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: background-color 0.1s ease, border-left 0.1s ease;
}

.user-item.active {
  background-color: #f0f8ff;
  border-left: 5px solid #007bff;
  box-shadow: 0 0 10px rgba(0, 123, 255, 0.2);
}

.user-item:hover {
  background-color: #f0f0f0;
}

.user-left {
  position: relative;
  margin-right: 10px;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.online-badge {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 12px;
  height: 12px;
  background-color: rgb(26, 196, 26);
  border-radius: 50%;
  border: 2px solid white;
  transform: translate(15%);
}


.user-info {
  flex: 1;
}

.username {
  font-size: 14px;
  font-weight: bold;
  color: #333;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.last-message {
  font-size: 12px;
  color: #888;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.last-message-text {
  display: inline-block;
  max-width: 150px;
  text-overflow: ellipsis;
  overflow: hidden;
  white-space: nowrap;
}

.user-time {
  position: absolute;
  right: 10px;
  top: 8px;
  font-size: 12px;
  color: #bbb;
}

.chat-status {
  font-size: 12px;
  color: limegreen;
  margin-left: 5px;
}


.unread-bubble {
  position: absolute;
  bottom: 10px;
  right: 12px;
  background-color: #ff4d4f;
  color: #fff;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 12px;
}



.last-message-preview {
  display: flex;
  font-size: 12px;
  color: #888;
  margin-top: 5px;
}

.last-message-content {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.last-message-time {
  margin-left: 10px;
  color: #bbb;
}




.user-item {
  position: relative;
  display: flex;
  align-items: center;
  margin: 10px 0;
}



.chat-box {
  flex: 1;
  position: relative;
  background-color: white;
  overflow: hidden;
}

.chat-header {
  text-align: center;
  line-height: 50px;
  border-bottom: 1px solid #ccc;
}

.status {
  font-size: 14px;
  color: #4caf50;
}

.status.online {
  color: #67C23A;
}

.status.offline {
  color: #F56C6C;
}

.message-container {
  height: 600px;
  overflow-y: scroll;
  padding: 15px;
}

.message-box {
  display: flex;
  flex-direction: column;
  margin: 20px 0;
  word-wrap: break-word;
  word-break: break-word;
}

.message-time {
  text-align: center;
  font-size: 12px;
  color: #888;
  white-space: nowrap;
  font-weight: bold;
  margin: 20px 0;
}


.message.right {
  display: flex;
  justify-content: flex-end;
}

.message.right .message-content {
  background-color: #007bff;
  color: white;
  padding: 10px;
  border-radius: 15px;
  max-width: 75%;
  position: relative;
  border: 1px solid #0056b3;

}

.message.right .avatar {
  margin-left: 10px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
}

.message.left {
  display: flex;
}

.message.left .message-content {
  background-color: #f1f1f1;
  color: black;
  padding: 10px;
  border-radius: 15px;
  max-width: 75%;
  position: relative;
  border: 1px solid #ccc;
}

.message.left .avatar {
  margin-right: 10px;
  border-radius: 50%;
  width: 40px;
  height: 40px;
}

.message-content:after {
  content: '';
  position: absolute;
  width: 0;
  height: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
}

.message.right .message-content:after {
  border-top: 10px solid #007bff;
  right: -10px;
  top: 50%;
  transform: translateY(-50%);
}

.message.left .message-content:after {
  border-bottom: 10px solid #f1f1f1;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
}

.message-content {
  word-wrap: break-word;
  overflow-wrap: break-word;
  white-space: normal;
}


.message-image {
  margin: 5px;
  border-radius: 5px;
  display: inline-block;
  max-width: 500px;
}



.message-file {
  display: inline-block;
  margin: 5px 10px;
  cursor: pointer;
  font-size: 14px;
  color: #409EFF;
}

.file-hint {
  font-size: 12px;
  color: #888;
}


.new-message-alert {
  position: absolute;
  bottom: 80px;
  right: 20px;
  background: linear-gradient(135deg, #ff7a00, #ff1c00);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  transition: all 0.3s ease;
  z-index: 10;
}

.new-message-alert:hover {
  background: linear-gradient(135deg, #ff4e00, #ff0000);
}


.content-editable {
  border: 1px solid #ccc;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 10px;
  min-height: 40px;
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


/* 自定义滚动条样式 */
::-webkit-scrollbar {
  width: 4px;
}

/* 滚动条的滑块样式 */
::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.0);
  /* 初始时透明的滑块 */
  border-radius: 10px;
}

/* 滚动条轨道样式 */
::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.0);
  /* 透明的轨道 */
}

/* 鼠标悬停在整个 .scroll-container 上时才显示滚动条 */
.scroll-container:hover::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.25);
}
</style>