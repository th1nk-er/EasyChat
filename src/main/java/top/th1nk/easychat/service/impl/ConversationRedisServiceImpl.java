package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.service.ConversationRedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ConversationRedisServiceImpl implements ConversationRedisService {
    private static final String CHAT_CONVERSATION_KEY = "chat:conversation:";
    @Resource
    private RedisTemplate<String, SysUserConversation> redisTemplate;

    /**
     * 处理发送者的聊天列表
     *
     * @param message 消息
     */
    private void handleMessageSent(SysChatMessage message) {
        // receiverId 收到消息，将 senderId 的对话记录 readCount 设为 0 表明已读
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey = CHAT_CONVERSATION_KEY + message.getSenderId();
        String hashKey = String.valueOf(message.getReceiverId());
        SysUserConversation userConversation = hashOperations.get(redisKey, hashKey);
        if (userConversation == null) {
            // redis中无记录
            userConversation = new SysUserConversation();
            userConversation.setUid(message.getSenderId());
            userConversation.setSenderId(message.getReceiverId());
            userConversation.setChatType(message.getChatType());
        }
        userConversation.setUnreadCount(0);
        userConversation.setUpdateTime(message.getCreateTime());
        userConversation.setLastMessage(message.getContent());
        userConversation.setMessageType(message.getMessageType());
        hashOperations.put(redisKey, hashKey, userConversation);
    }

    @Override
    public void handleMessageReceived(SysChatMessage message) {
        // receiverId 收到消息，将 receiverId 的对话记录 readCount + 1
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey = CHAT_CONVERSATION_KEY + message.getReceiverId();
        String hashKey = String.valueOf(message.getSenderId());
        SysUserConversation userConversation = hashOperations.get(redisKey, hashKey);
        if (userConversation == null) {
            // redis中无记录
            userConversation = new SysUserConversation();
            userConversation.setUid(message.getReceiverId());
            userConversation.setSenderId(message.getSenderId());
            userConversation.setChatType(message.getChatType());
            userConversation.setUnreadCount(1);
        } else {
            // redis中有记录
            userConversation.setUnreadCount(userConversation.getUnreadCount() + 1);
        }
        userConversation.setUpdateTime(message.getCreateTime());
        userConversation.setLastMessage(message.getContent());
        userConversation.setMessageType(message.getMessageType());
        hashOperations.put(redisKey, hashKey, userConversation);
        handleMessageSent(message);
    }

    @Override
    public List<SysUserConversation> getUserConversations(int uid) {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        return hashOperations.values(CHAT_CONVERSATION_KEY + uid);
    }

    @Override
    public List<SysUserConversation> getAllUserConversations() {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        Set<String> keys = redisTemplate.keys(CHAT_CONVERSATION_KEY + "*");
        if (keys == null || keys.isEmpty()) return List.of();
        List<SysUserConversation> result = new ArrayList<>();
        for (String key : keys) {
            result.addAll(hashOperations.values(key));
        }
        return result;
    }

    @Override
    public boolean setConversationRead(int senderId, int receiverId) {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey = CHAT_CONVERSATION_KEY + senderId;
        String hashKey = String.valueOf(receiverId);
        SysUserConversation sysUserConversation = hashOperations.get(redisKey, hashKey);
        if (sysUserConversation == null) return false;
        sysUserConversation.setUnreadCount(0);
        hashOperations.put(redisKey, hashKey, sysUserConversation);
        return true;
    }

    @Override
    public void addToConversation(List<SysUserConversation> conversation) {
        if (conversation == null || conversation.isEmpty()) return;
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        for (SysUserConversation userConversation : conversation) {
            String redisKey = CHAT_CONVERSATION_KEY + userConversation.getUid();
            String hashKey = String.valueOf(userConversation.getSenderId());
            hashOperations.put(redisKey, hashKey, userConversation);
        }
    }

    @Override
    public void deleteFriendConversation(int senderId, int receiverId) {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        hashOperations.delete(CHAT_CONVERSATION_KEY + senderId, String.valueOf(receiverId));
    }
}
