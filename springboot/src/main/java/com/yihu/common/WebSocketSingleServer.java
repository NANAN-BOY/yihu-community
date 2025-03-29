package com.yihu.common;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.yihu.entity.Communication;
import com.yihu.entity.User;
import com.yihu.service.CommunicationService;
import com.yihu.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/imserverSingle")
@Component
public class WebSocketSingleServer implements InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(WebSocketSingleServer.class);

    // 记录用户ID与 Session 的映射
    public static final Map<Integer, Session> userSessionMap = new ConcurrentHashMap<>();

    @Resource
    private CommunicationService communicationService;
    @Resource
    private UserMapper userMapper;

    static CommunicationService staticCommunicationService;
    static UserMapper staticUserMapper;

    /**
     * 连接建立时验证 Token 并绑定用户（基于用户ID）
     */
    @OnOpen
    public void onOpen(Session session) {
        try {
            // 1. 从 URL 参数获取 Token
            List<String> tokens = session.getRequestParameterMap().get("token");
            if (tokens == null || tokens.isEmpty()) {
                closeSession(session, "未提供 Token");
                return;
            }
            String token = tokens.get(0);

            // 2. 解析 Token 获取用户信息（格式：id|phone|role）
            String audience = JWT.decode(token).getAudience().get(0);
            String[] parts = audience.split("\\|");
            if (parts.length != 3) { // 检查分割后的字段数量
                closeSession(session, "Token 格式错误");
                return;
            }

            // 提取用户ID、手机号、角色
            Integer userId = Integer.parseInt(parts[0]);
            String phone = parts[1];
            Integer role = Integer.parseInt(parts[2]);

            User user = staticUserMapper.login(phone);
            if (user == null) {
                closeSession(session, "用户不存在");
                return;
            }

            // 4. 绑定用户ID与 Session
            userSessionMap.put(userId, session);
            log.info("[onOpen] 用户连接成功: userId={}, phone={}, 当前在线数: {}", userId, phone, userSessionMap.size());

        } catch (NumberFormatException e) {
            log.error("Token 解析失败：用户ID或角色格式错误", e);
            closeSession(session, "Token 无效");
        } catch (Exception e) {
            log.error("Token 验证失败", e);
            closeSession(session, "Token 无效");
        }
    }

    /**
     * 收到消息时定向发送给接收方（基于用户ID）
     */
    @OnMessage
    public void onMessage(String message, Session fromSession) {
        Communication communication = JSONUtil.toBean(message, Communication.class);
        communication.setTime(new Date());
        System.out.println("收信人：" + communication.getReceiveUserId() + "发信人：" + communication.getSendUserId());

        // 持久化到数据库
        staticCommunicationService.add(communication);

        // 定向发送给接收方（使用用户ID）
        sendMessage(communication.getReceiveUserId(), JSONUtil.toJsonStr(communication));
    }

    /**
     * 关闭连接时清理用户绑定
     */
    @OnClose
    public void onClose(Session session) {
        Integer userId = getKeyByValue(userSessionMap, session);
        if (userId != null) {
            userSessionMap.remove(userId);
            log.info("[onClose] 用户断开连接: userId={}, 当前在线数: {}", userId, userSessionMap.size());
        }
    }

    /**
     * 定向发送消息给指定用户
     */
    private void sendMessage(Integer receiveUserId, String message) {
        if (receiveUserId == null) {
            log.error("发送失败：接收方用户ID不能为空");
            return;
        }

        Session session = userSessionMap.get(receiveUserId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.info("消息已发送给用户 [{}]: {}", receiveUserId, message);
            } catch (IOException e) {
                log.error("发送消息失败", e);
            }
        } else {
            log.warn("用户 [{}] 不在线，消息已存储", receiveUserId);
        }
    }

    @Override
    public void afterPropertiesSet() {
        staticCommunicationService = communicationService;
        staticUserMapper = userMapper;
    }

    // 工具方法：根据 Session 查找对应的用户ID
    private Integer getKeyByValue(Map<Integer, Session> map, Session value) {
        for (Map.Entry<Integer, Session> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // 关闭连接并记录原因
    private void closeSession(Session session, String reason) {
        try {
            session.close(new CloseReason(CloseReason.CloseCodes.VIOLATED_POLICY, reason));
            log.warn("连接关闭: {}", reason);
        } catch (IOException e) {
            log.error("关闭连接异常", e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket 异常", error);
    }
}