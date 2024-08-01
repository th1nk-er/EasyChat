package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import top.th1nk.easychat.domain.chat.ChatMessage;
import top.th1nk.easychat.service.WebSocketService;

@Controller
@Slf4j
@Tag(name = "聊天模块", description = "聊天模块API")
public class ChatController {
    @Resource
    private WebSocketService webSocketService;

    @Operation(summary = "用户尝试连接", description = "用户尝试连接")
    @MessageMapping("/notify/connect")
    public void notifyMessage(SimpMessageHeaderAccessor accessor) {
        Authentication user = (Authentication) accessor.getUser();
        if (user == null)
            return;
        webSocketService.handleUserConnected(user);
    }

    @Operation(summary = "用户发送消息", description = "用户发送消息")
    @MessageMapping("/message/chat.send")
    public void sendMessage(ChatMessage message, SimpMessageHeaderAccessor accessor) {
        Authentication user = (Authentication) accessor.getUser();
        if (user == null)
            return;
        webSocketService.sendUserMessage(user, message);
    }
}
