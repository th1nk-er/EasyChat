package top.th1nk.easychat.service;

import org.springframework.security.core.Authentication;
import top.th1nk.easychat.domain.chat.ChatMessage;

public interface WebSocketService {

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(ChatMessage message);

    /**
     * 处理用户连接
     *
     * @param authentication 用户认证信息
     */
    void handleUserConnected(Authentication authentication);

    /**
     * 用户发送消息
     *
     * @param message 消息
     */
    void sendUserMessage(Authentication authentication, ChatMessage message);
}
