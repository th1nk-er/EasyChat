package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.MessageRedisService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class MessageRedisServiceImpl implements MessageRedisService {
    private static final String CHAT_PRIVATE_MESSAGE_KEY = "chat:message:private:";
    private static final String CHAT_GROUP_MESSAGE_KEY = "chat:message:group:";

    @Resource
    private RedisTemplate<String, SysChatMessage> redisTemplate;

    @Resource
    private ConversationRedisService conversationRedisService;

    private String getRedisKey(int senderId, int receiverId) {
        if (senderId < receiverId)
            return CHAT_PRIVATE_MESSAGE_KEY + senderId + "-" + receiverId;
        else
            return CHAT_PRIVATE_MESSAGE_KEY + receiverId + "-" + senderId;
    }

    @Override
    public int saveMessage(WSMessage wsMessage) {
        SysChatMessage message = new SysChatMessage();
        message.setContent(wsMessage.getContent());
        message.setMessageType(wsMessage.getMessageType());
        message.setChatType(wsMessage.getChatType());
        message.setSenderId(wsMessage.getFromId());
        message.setReceiverId(wsMessage.getToId());
        message.setCreateTime(LocalDateTime.now());
        String chatKey;
        if (message.getChatType() == ChatType.FRIEND) {
            chatKey = getRedisKey(message.getSenderId(), message.getReceiverId());
        } else if (message.getChatType() == ChatType.GROUP)
            chatKey = CHAT_GROUP_MESSAGE_KEY + message.getReceiverId();
        else {
            return 0;
        }
        redisTemplate.opsForList().rightPush(chatKey, message);
        Long size = redisTemplate.opsForList().size(chatKey);
        conversationRedisService.handleMessageReceived(message);
        return size == null ? 0 : size.intValue();
    }

    @Override
    public List<SysChatMessage> getMessages(int senderId, int receiverId) {
        String chatKey = getRedisKey(senderId, receiverId);
        return redisTemplate.opsForList().range(chatKey, 0, -1);
    }

    @Override
    public void removeMessage(int senderId, int receiverId) {
        String chatKey = getRedisKey(senderId, receiverId);
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
