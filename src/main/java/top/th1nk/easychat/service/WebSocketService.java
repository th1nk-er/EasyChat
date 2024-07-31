package top.th1nk.easychat.service;

import top.th1nk.easychat.domain.chat.ChatMessage;

public interface WebSocketService {

    void sendMessage(ChatMessage message);
}
