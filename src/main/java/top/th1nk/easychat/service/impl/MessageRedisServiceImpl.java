package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class MessageRedisServiceImpl implements MessageRedisService {
    private static final String CHAT_PRIVATE_MESSAGE_KEY = "chat:message:private:";
    private static final String CHAT_GROUP_MESSAGE_KEY = "chat:message:group:";

    @Resource
    private RedisTemplate<String, SysChatMessage> redisTemplate;

    @Resource
    private ConversationRedisService conversationRedisService;

    private String getRedisKey(int userId, int chatId, ChatType chatType) {
        if (chatType == ChatType.FRIEND) {
            if (userId < chatId)
                return CHAT_PRIVATE_MESSAGE_KEY + userId + "-" + chatId;
            else
                return CHAT_PRIVATE_MESSAGE_KEY + chatId + "-" + userId;
        } else if (chatType == ChatType.GROUP)
            return CHAT_GROUP_MESSAGE_KEY + chatId;
        else {
            return "";
        }
    }

    @Override
    public int saveMessage(WSMessage wsMessage) {
        SysChatMessage message = new SysChatMessage();
        message.setParams(wsMessage.getParams().toString());
        message.setContent(wsMessage.getContent());
        message.setMessageType(wsMessage.getMessageType());
        message.setChatType(wsMessage.getChatType());
        message.setSenderId(wsMessage.getFromId());
        message.setReceiverId(wsMessage.getToId());
        message.setCreateTime(LocalDateTime.now());
        String chatKey = getRedisKey(message.getSenderId(), message.getReceiverId(), message.getChatType());
        redisTemplate.opsForList().rightPush(chatKey, message);
        Long size = redisTemplate.opsForList().size(chatKey);
        conversationRedisService.handleMessageReceived(message);
        return size == null ? 0 : size.intValue();
    }

    @Override
    public List<SysChatMessage> getMessages(int userId, int chatId, ChatType chatType) {
        String chatKey = getRedisKey(userId, chatId, chatType);
        return redisTemplate.opsForList().range(chatKey, 0, -1);
    }

    @Override
    public void removeMessage(int userId, int chatId, ChatType chatType) {
        String chatKey = getRedisKey(userId, chatId, chatType);
        redisTemplate.delete(chatKey);
    }

    @Override
    public List<SysChatMessage> getAllMessages() {
        Set<String> keys = redisTemplate.keys(CHAT_PRIVATE_MESSAGE_KEY + "*");
        Set<String> groupKeys = redisTemplate.keys(CHAT_GROUP_MESSAGE_KEY + "*");
        if (keys == null || groupKeys == null) {
            log.error("redis异常，无法获取消息");
            return List.of();
        }
        keys.addAll(groupKeys);
        List<SysChatMessage> result = new ArrayList<>();
        for (String key : keys) {
            List<SysChatMessage> part = redisTemplate.opsForList().range(key, 0, -1);
            if (part != null && !part.isEmpty())
                result.addAll(part);
        }
        return result;
    }
}
