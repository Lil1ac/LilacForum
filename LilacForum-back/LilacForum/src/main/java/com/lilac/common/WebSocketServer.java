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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务端
 */
@ServerEndpoint("/chat/{username}")
@Component
public class WebSocketServer implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    /**
     * 记录当前在线连接数
     */
    public static final Map<String, Session> onlineUsers = new ConcurrentHashMap<>();

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
//        JSONObject result = new JSONObject();
//        JSONArray array = new JSONArray();
//        result.set("users", array);
//        for (Object key : onlineUsers.keySet()) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.set("username", key);
//            array.add(jsonObject);
//        }
//        sendAllMessage(JSONUtil.toJsonStr(result));  // 后台发送消息给所有的客户端
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        onlineUsers.remove(session.getId(),session);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, onlineUsers.size());
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到消息：{}",message);
        ImSingle imSingle = JSONUtil.toBean(message, ImSingle.class);
        imSingle.setTime(LocalDateTime.now());
        //存储数据到数据库
        staticImSingleService.add(imSingle);

        // 处理消息
        User fromUser= staticUserService.getUserById(imSingle.getFromUserId());
        User toUser= staticUserService.getUserById(imSingle.getToUserId());
        ImSingleRequest imSingleRequest= new ImSingleRequest(
                imSingle, toUser.getAvatar(), toUser.getUsername(), fromUser.getAvatar(), fromUser.getUsername());
        String jsonStr = JSONUtil.toJsonStr(imSingleRequest); // 处理后的消息体
        this.sendAllMessage(jsonStr);
        log.info("[onMessage] 发送消息：{}",jsonStr);
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
            if(fromSession!= toSession){
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

    @Override
    public void afterPropertiesSet() throws Exception {
        staticImSingleService = imSingleService;
        staticUserService = userService;
    }
}
