package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import top.th1nk.easychat.domain.chat.ChatMessage;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.service.WebSocketService;

@Controller
@Schema(description = "聊天模块")
@Slf4j
public class ChatController {
    @Resource
    private WebSocketService webSocketService;

    @MessageMapping("/notify/connect")
    public void notifyMessage(SimpMessageHeaderAccessor accessor) {
        Authentication user = (Authentication) accessor.getUser();
        if (user == null)
            return;
        String token = user.getCredentials().toString();
        webSocketService.sendMessage(ChatMessage.command(token, MessageCommand.CONNECTED));
        log.info("WebSocket User connect:{}", user.getPrincipal().toString());
    }
}
