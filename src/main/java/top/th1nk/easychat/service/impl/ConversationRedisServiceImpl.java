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
    private static final String CHAT_HISTORY_KEY = "chat:history:";
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
        String redisKey = CHAT_HISTORY_KEY + message.getSenderId();
        String hashKey = String.valueOf(message.getReceiverId());
        SysUserConversation chatHistory = hashOperations.get(redisKey, hashKey);
        if (chatHistory == null) {
            chatHistory = new SysUserConversation();
            chatHistory.setUid(message.getSenderId());
            chatHistory.setFriendId(message.getReceiverId());
            chatHistory.setGroupId(message.getSenderGroupId());
            chatHistory.setUpdateTime(message.getCreateTime());
            chatHistory.setLastMessage(message.getContent());
            chatHistory.setMessageType(message.getType());
        }
        chatHistory.setUnreadCount(0);
        hashOperations.put(redisKey, hashKey, chatHistory);
    }

    @Override
    public void handleMessageReceived(SysChatMessage message) {
        // receiverId 收到消息，将 receiverId 的对话记录 readCount + 1
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey = CHAT_HISTORY_KEY + message.getReceiverId();
        String hashKey = String.valueOf(message.getSenderId());
        SysUserConversation chatHistory = hashOperations.get(redisKey, hashKey);
        if (chatHistory == null) {
            chatHistory = new SysUserConversation();
            chatHistory.setUid(message.getReceiverId());
            chatHistory.setFriendId(message.getSenderId());
            chatHistory.setGroupId(message.getSenderGroupId());
            chatHistory.setUpdateTime(message.getCreateTime());
            chatHistory.setLastMessage(message.getContent());
            chatHistory.setMessageType(message.getType());
            chatHistory.setUnreadCount(1);
        } else {
            chatHistory.setUnreadCount(chatHistory.getUnreadCount() + 1);
        }
        hashOperations.put(redisKey, hashKey, chatHistory);
        handleMessageSent(message);
    }

    @Override
    public List<SysUserConversation> getUserConversations(int uid) {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        return hashOperations.values(CHAT_HISTORY_KEY + uid);
    }

    @Override
    public List<SysUserConversation> getAllUserConversations() {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        Set<String> keys = redisTemplate.keys(CHAT_HISTORY_KEY + "*");
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
        String redisKey = CHAT_HISTORY_KEY + senderId;
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
            String redisKey = CHAT_HISTORY_KEY + userConversation.getUid();
            String hashKey = String.valueOf(userConversation.getFriendId());
            hashOperations.put(redisKey, hashKey, userConversation);
        }
    }

}
