package top.th1nk.easychat.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.Authentication;
import top.th1nk.easychat.domain.chat.WSMessage;

public interface WebSocketService {

    /**
     * 发送消息
     *
     * @param message 消息
     */
    void sendMessage(WSMessage message);

    /**
     * 处理用户连接
     *
     * @param authentication 用户认证信息
     */
    void handleUserConnected(@NotNull Authentication authentication);

    /**
     * 用户发送消息
     *
     * @param message 消息
     */
    void sendUserMessage(Authentication authentication, WSMessage message);

    /**
     * 处理用户断开连接
     *
     * @param authentication 用户认证信息
     */
    void handleUserDisconnected(@NotNull Authentication authentication);
}
