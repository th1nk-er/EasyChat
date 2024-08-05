package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.service.MessageRedisService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MessageRedisServiceImpl implements MessageRedisService {
    private static final String CHAT_PRIVATE_MESSAGE_KEY = "chat:message:private:";

    @Resource
    private RedisTemplate<String, SysChatMessage> redisTemplate;

    @Override
    public int saveMessage(WSMessage wsMessage) {
        SysChatMessage message = new SysChatMessage();
        message.setContent(wsMessage.getContent());
        message.setType(wsMessage.getType());
        message.setReceiverId(wsMessage.getToId() == null ? wsMessage.getGroupId() : wsMessage.getToId());
        message.setSenderId(wsMessage.getFromId());
        message.setCreateTime(LocalDateTime.now());
        String chatKey;
        if (wsMessage.getToId() != null) {
            if (message.getSenderId() < message.getReceiverId()) {
                chatKey = CHAT_PRIVATE_MESSAGE_KEY + message.getSenderId() + "-" + message.getReceiverId();
            } else {
                chatKey = CHAT_PRIVATE_MESSAGE_KEY + message.getReceiverId() + "-" + message.getSenderId();
            }
        } else
            chatKey = CHAT_PRIVATE_MESSAGE_KEY + message.getSenderId() + "-" + message.getReceiverId();
        redisTemplate.opsForList().rightPush(chatKey, message);
        Long size = redisTemplate.opsForList().size(chatKey);
        return size == null ? 0 : size.intValue();
    }

    public List<SysChatMessage> getMessages(int senderId, int receiverId) {
        String chatKey;
        if (senderId < receiverId) {
            chatKey = CHAT_PRIVATE_MESSAGE_KEY + senderId + "-" + receiverId;
        } else {
            chatKey = CHAT_PRIVATE_MESSAGE_KEY + receiverId + "-" + senderId;
        }
        return redisTemplate.opsForList().range(chatKey, 0, -1);
    }

    @Override
    public void removeMessage(int senderId, int receiverId) {
        String chatKey;
        if (senderId < receiverId) {
            chatKey = CHAT_PRIVATE_MESSAGE_KEY + senderId + "-" + receiverId;
        } else {
            chatKey = CHAT_PRIVATE_MESSAGE_KEY + receiverId + "-" + senderId;
        }
        redisTemplate.delete(chatKey);
    }

    @Override
    public List<SysChatMessage> getAllMessages() {
        Set<String> keys = redisTemplate.keys(CHAT_PRIVATE_MESSAGE_KEY + "*");
        if (keys == null) return List.of();
        List<SysChatMessage> result = new ArrayList<>();
        for (String key : keys) {
            List<SysChatMessage> part = redisTemplate.opsForList().range(key, 0, -1);
            if (part != null && !part.isEmpty())
                result.addAll(part);
        }
        return result;
    }
}
