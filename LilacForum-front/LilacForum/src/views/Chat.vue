<template>
  <div style="padding: 10px; margin-bottom: 50px">
    <el-row>
      <el-col :span="8">
        <el-card style="width: 100%; min-height: 300px; color: #333">
          <div style="padding-bottom: 10px; border-bottom: 1px solid #ccc">在线用户
            <span style="font-size: 12px">（点击聊天气泡开始聊天）</span>
          </div>
          <div style="padding: 10px 0" v-for="user in users" :key="user.username">
            <span>{{ user.username }}</span>
               <el-icon class="el-icon-chat-dot-round" style="margin-left: 10px; font-size: 16px; cursor: pointer"
               @click="chatUser = user.username"><ChatDotRound /></el-icon>
            <span style="font-size: 12px;color: limegreen; margin-left: 5px" v-if="user.username === chatUser">chatting...</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="16">
        <div style="width: 800px; margin: 0 auto; background-color: white; border-radius: 5px; box-shadow: 0 0 10px #ccc">
          <div style="text-align: center; line-height: 50px;">
            Web聊天室（{{ chatUser }}）
          </div>
          <div style="height: 350px; overflow:auto; border-top: 1px solid #ccc" v-html="content"></div>
          <div style="height: 200px">
            <textarea v-model="text" style="height: 160px; width: 100%; padding: 20px; border: none; border-top: 1px solid #ccc; border-bottom: 1px solid #ccc; outline: none"></textarea>
            <div style="text-align: right; padding-right: 10px">
              <el-button type="primary" size="small" @click="send">发送</el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from 'vue';
import * as ElIcons from '@element-plus/icons-vue';

interface User {
  username: string;
}

interface Message {
  from: string;
  to: string;
  text: string;
}

export default defineComponent({
  name: 'Chat',
  setup() {
    const circleUrl = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
    const user = ref<{ username: string }>({ username: '' });
    const users = ref<User[]>([]);
    const chatUser = ref<string>('');
    const text = ref<string>('');
    const messages = ref<Message[]>([]);
    const content = ref<string>('');

    let socket: WebSocket | null = null;

    const send = () => {
      if (!chatUser.value) {
        console.log('请选择聊天对象');
        return;
      }
      if (!text.value) {
        console.log('请输入内容');
      } else {
        if (typeof WebSocket === 'undefined') {
          console.log('您的浏览器不支持WebSocket');
        } else {
          console.log('您的浏览器支持WebSocket');
          // 组装待发送的消息 json
          const message: Message = { from: user.value.username, to: chatUser.value, text: text.value };
          if (socket) {
            socket.send(JSON.stringify(message)); // 将组装好的json发送给服务端，由服务端进行转发
            messages.value.push({ from: user.value.username, to: chatUser.value, text: text.value });
            createContent(null, user.value.username, text.value);
            text.value = '';
          }
        }
      }
    };

    const createContent = (remoteUser: string | null, nowUser: string, text: string) => {
      let html = '';
      if (nowUser) { // 当前用户消息，绿色气泡
        html = `<div class="el-row" style="padding: 5px 0">
                  <div class="el-col el-col-22" style="text-align: right; padding-right: 10px">
                    <div class="tip left">${text}</div>
                  </div>
                  <div class="el-col el-col-2">
                    <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px;">
                      <img src="${circleUrl}" style="object-fit: cover;">
                    </span>
                  </div>
                </div>`;
      } else if (remoteUser) { // 远程用户消息，蓝色气泡
        html = `<div class="el-row" style="padding: 5px 0">
                  <div class="el-col el-col-2" style="text-align: right">
                    <span class="el-avatar el-avatar--circle" style="height: 40px; width: 40px; line-height: 40px;">
                      <img src="${circleUrl}" style="object-fit: cover;">
                    </span>
                  </div>
                  <div class="el-col el-col-22" style="text-align: left; padding-left: 10px">
                    <div class="tip right">${text}</div>
                  </div>
                </div>`;
      }
      content.value += html;
    };

    const init = () => {
      user.value = JSON.parse(localStorage.getItem('user') || '{}');
      console.log('user:', user.value);
      const username = user.value.username;
      if (typeof WebSocket === 'undefined') {
        console.log('您的浏览器不支持WebSocket');
      } else {
        console.log('您的浏览器支持WebSocket');
        const socketUrl = `ws://localhost:8080/chat/${username}`;

        if (socket) {
          socket.close();
          socket = null;
        }

        socket = new WebSocket(socketUrl);
        socket.onopen = () => {
          console.log('websocket已打开');
        };

        socket.onmessage = (msg) => {
          console.log('收到数据====' + msg.data);
          const data = JSON.parse(msg.data); // 对收到的json数据进行解析，类似这样的：{"users": [{"username": "zhang"},{ "username": "admin"}]}
          if (data.users) {
            users.value = data.users.filter((user: User) => user.username !== username); // 获取当前连接的所有用户信息，并且排除自身
          } else {
            if (data.from === chatUser.value) {
              messages.value.push(data);
              createContent(data.from, '', data.text);
            }
          }
        };

        socket.onclose = () => {
          console.log('websocket已关闭');
        };

        socket.onerror = () => {
          console.log('websocket发生了错误');
        };
      }
    };

    onMounted(() => {
      init();
    });

    return {
      circleUrl,
      user,
      users,
      chatUser,
      text,
      messages,
      content,
      send
    };
  }
});
</script>

<style scoped>
.tip {
  color: white;
  text-align: center;
  border-radius: 10px;
  font-family: sans-serif;
  padding: 10px;
  width:auto;
  display:inline-block !important;
  display:inline;
}
.right {
  background-color: deepskyblue;
}
.left {
  background-color: forestgreen;
}
</style>
