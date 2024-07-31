package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.chat.ChatMessage;
import top.th1nk.easychat.service.WebSocketService;

@Service
public class WebSocketServiceImpl implements WebSocketService {
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendMessage(ChatMessage message) {
        simpMessagingTemplate.convertAndSend("/notify/message/" + message.getToId(), message);
    }
}
