package com.lilac.common;

import cn.hutool.json.JSONUtil;
import com.lilac.dto.ImSingleRequest;
import com.lilac.pojo.ImSingle;
import com.lilac.pojo.User;
import com.lilac.service.impl.ImSingleServiceImpl;
import com.lilac.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// WebSocket 服务端

@ServerEndpoint("/chat/{username}")
@Component
public class WebSocketServer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    //记录当前在线连接数
    public static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();
    // 记录每个用户的在线状态
    public static final Map<String, Boolean> userOnlineStatus = new ConcurrentHashMap<>();
    @Resource
    ImSingleServiceImpl imSingleService;

    static ImSingleServiceImpl staticImSingleService;

    @Resource
    UserServiceImpl userService;

    static UserServiceImpl staticUserService;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        onlineUsers.put(session.getId(), session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", username, onlineUsers.size());
        // 标记该用户为在线
        userOnlineStatus.put(username, true);
        // 向所有客户端广播当前在线用户列表
        sendOnlineUsers();
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        onlineUsers.remove(session.getId(), session);
        userOnlineStatus.put(username, false);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, onlineUsers.size());
        // 向所有客户端广播当前在线用户列表
        sendOnlineUsers();
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到消息：{}", message);

        // 解析消息
        Map<String, Object> messageMap = JSONUtil.toBean(message, Map.class);
        String type = (String) messageMap.get("type");  // 获取消息类型
        Object data = messageMap.get("data");  // 获取消息内容

        // 根据消息类型来处理
        if ("chat".equals(type)) {
            // 处理聊天消息
            handleChatMessage(data);
        } else if ("status".equals(type)) {
            // 处理在线状态更新消息
            handleOnlineStatusUpdate(data);
        } else if ("getOnlineStatus".equals(type)) {
            // 返回所有在线用户的状态
            sendOnlineUsers();  // 向客户端发送在线用户列表
        } else {
            log.warn("未知的消息类型: {}", type);
        }
    }

    private void handleChatMessage(Object data) {
        // 转换为具体的消息对象
        ImSingle imSingle = JSONUtil.toBean(data.toString(), ImSingle.class);
        imSingle.setTime(LocalDateTime.now());

        // 存储数据到数据库
        staticImSingleService.add(imSingle);

        // 获取用户信息
        User fromUser = staticUserService.getUserById(imSingle.getFromUserId());
        User toUser = staticUserService.getUserById(imSingle.getToUserId());

        // 创建消息请求对象
        ImSingleRequest imSingleRequest = new ImSingleRequest(
                imSingle, toUser.getAvatar(), toUser.getUsername(), fromUser.getAvatar(), fromUser.getUsername());

        // 创建包含 type 的包装消息
        Map<String, Object> message = new ConcurrentHashMap<>();
        message.put("type", "chat");  // 设置消息类型
        message.put("data", imSingleRequest);  // 将 ImSingleRequest 放到 data 字段

        // 转换为 JSON 字符串
        String jsonStr = JSONUtil.toJsonStr(message);

        // 发送消息给所有客户端
        this.sendAllMessage(jsonStr);
        log.info("[handleChatMessage] 发送聊天消息：{}", jsonStr);
    }


    private void handleOnlineStatusUpdate(Object data) {
        // 转换为在线状态更新信息
        Map<String, Boolean> userStatus = (Map<String, Boolean>) data;

        // 广播更新的在线状态信息
        String jsonStr = JSONUtil.toJsonStr(userStatus);
        this.sendAllMessage(jsonStr);
        log.info("[handleOnlineStatusUpdate] 发送在线状态更新：{}", jsonStr);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session fromSession) {
        onlineUsers.values().forEach(toSession -> {
            if (fromSession != toSession) {
                try {
                    log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
                    toSession.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.error("服务端发送消息给客户端失败", e);
                }
            }
        });
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        try {
            for (Session session : onlineUsers.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    // 向所有客户端广播在线用户列表（包含在线状态）
    // 向所有客户端广播在线用户列表（包含在线状态）
    private void sendOnlineUsers() {
        try {
            // 获取在线用户并记录其状态（在线/离线）
            Map<String, Object> onlineUsersStatus = new ConcurrentHashMap<>();
            for (String username : userOnlineStatus.keySet()) {
                onlineUsersStatus.put(username, userOnlineStatus.get(username));
            }

            // 创建消息对象
            Map<String, Object> responseMessage = new ConcurrentHashMap<>();
            responseMessage.put("type", "status");  // 设置消息类型为 'status'
            responseMessage.put("data", onlineUsersStatus);  // 设置在线用户状态数据

            // 将消息转换为 JSON 字符串
            String onlineUserListJson = JSONUtil.toJsonStr(responseMessage);

            // 向所有客户端发送当前在线用户列表（包含在线/离线状态）
            for (Session session : onlineUsers.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), onlineUserListJson);
                session.getBasicRemote().sendText(onlineUserListJson);
            }
        } catch (Exception e) {
            log.error("广播在线用户列表失败", e);
        }
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        staticImSingleService = imSingleService;
        staticUserService = userService;
    }
}
