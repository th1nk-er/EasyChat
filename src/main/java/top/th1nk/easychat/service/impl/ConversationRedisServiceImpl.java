package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.vo.GroupMemberVo;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.service.ConversationRedisService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class ConversationRedisServiceImpl implements ConversationRedisService {
    // 使用两组key原因：
    // 1.当用户ID与群组ID相同时，区分是好友对话和群组对话
    // 2.便于管理
    private static final String CHAT_CONVERSATION_PRIVATE_KEY = "chat:conversation:private:";
    private static final String CHAT_CONVERSATION_GROUP_KEY = "chat:conversation:group:";
    @Resource
    private RedisTemplate<String, SysUserConversation> redisTemplate;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;

    /**
     * 获取redis key
     *
     * @param userId   用户ID
     * @param chatType 聊天类型
     * @return redis key
     */
    private String getRedisKey(int userId, ChatType chatType) {
        if (chatType == ChatType.FRIEND)
            return CHAT_CONVERSATION_PRIVATE_KEY + userId;
        else if (chatType == ChatType.GROUP)
            return CHAT_CONVERSATION_GROUP_KEY + userId;
        else
            return "";
    }

    /**
     * 获取对话
     *
     * @param userId   用户ID
     * @param chatId   对话ID
     * @param chatType 聊天类型
     * @return 对话
     */
    @Nullable
    private SysUserConversation getConversation(int userId, int chatId, ChatType chatType) {
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey = getRedisKey(userId, chatType);
        return hashOperations.get(redisKey, String.valueOf(chatId));
    }

    /**
     * 更新好友对话
     * 将 senderId 对话的 unreadCount设置为0
     * 将 receiverId 对话的 unreadCount + 1
     *
     * @param message 消息
     */
    private void updateFriendConversation(SysChatMessage message) {
        if (message.getChatType() != ChatType.FRIEND) return;
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        // 更新 receiverId 的对话
        SysUserConversation userConversation = getConversation(message.getReceiverId(), message.getSenderId(), ChatType.FRIEND);
        if (userConversation == null) {
            userConversation = new SysUserConversation();
            userConversation.setUid(message.getReceiverId());
            userConversation.setChatId(message.getSenderId());
            userConversation.setChatType(ChatType.FRIEND);
            userConversation.setUnreadCount(1);
        } else {
            userConversation.setUnreadCount(userConversation.getUnreadCount() + 1);
        }
        userConversation.setUpdateTime(message.getCreateTime());
        userConversation.setLastMessage(message.getContent());
        hashOperations.put(getRedisKey(message.getReceiverId(), ChatType.FRIEND), String.valueOf(message.getSenderId()), userConversation);
        // 更新 senderId 的对话
        userConversation = getConversation(message.getSenderId(), message.getReceiverId(), ChatType.FRIEND);
        if (userConversation == null) {
            userConversation = new SysUserConversation();
            userConversation.setUid(message.getSenderId());
            userConversation.setChatId(message.getReceiverId());
            userConversation.setChatType(ChatType.FRIEND);
        }
        userConversation.setUnreadCount(0);
        userConversation.setUpdateTime(message.getCreateTime());
        userConversation.setLastMessage(message.getContent());
        userConversation.setMessageType(message.getMessageType());
        hashOperations.put(getRedisKey(message.getSenderId(), ChatType.FRIEND), String.valueOf(message.getReceiverId()), userConversation);

    }

    /**
     * 更新群组对话
     * 将用户的群组对话的unreadCount + 1
     *
     * @param message 消息
     * @param userId  用户ID
     */
    private void updateGroupConversation(SysChatMessage message, int userId) {
        if (message.getChatType() != ChatType.GROUP) return;
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        // userId 收到群组的消息 message
        SysUserConversation userConversation = getConversation(userId, message.getReceiverId(), ChatType.GROUP);
        if (userConversation == null) {
            userConversation = new SysUserConversation();
            userConversation.setUid(userId);
            userConversation.setChatId(message.getReceiverId());
            userConversation.setChatType(ChatType.GROUP);
            userConversation.setUnreadCount(1);
        } else {
            userConversation.setUnreadCount(userConversation.getUnreadCount() + 1);
        }
        if (message.getSenderId() == userId) userConversation.setUnreadCount(0);
        userConversation.setUpdateTime(message.getCreateTime());
        userConversation.setLastMessage(message.getContent());
        userConversation.setMessageType(message.getMessageType());
        hashOperations.put(getRedisKey(userId, ChatType.GROUP), String.valueOf(message.getReceiverId()), userConversation);
    }

    @Override
    public void handleMessageReceived(SysChatMessage message) {
        log.debug("更新redis中用户对话列表,message:{}", message);
        if (message.getChatType() == ChatType.GROUP) {
            // 更新每一个群聊成员的对话
            // Page current = -1 表示不分页
            List<GroupMemberVo> groupMemberVos = sysGroupMemberMapper.selectGroupMemberVo(new Page<>(-1, 10), message.getReceiverId());
            groupMemberVos.forEach(vo -> updateGroupConversation(message, vo.getUserId()));
        } else if (message.getChatType() == ChatType.FRIEND) {
            updateFriendConversation(message);
        }
    }

    @Override
    public List<SysUserConversation> getUserConversations(int uid) {
        log.debug("从redis获取对话列表,userId:{}", uid);
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        List<SysUserConversation> res = hashOperations.values(CHAT_CONVERSATION_PRIVATE_KEY + uid);
        res.addAll(hashOperations.values(CHAT_CONVERSATION_GROUP_KEY + uid));
        res.sort((o1, o2) -> o2.getUpdateTime().compareTo(o1.getUpdateTime()));
        return res;
    }

    @Override
    public List<SysUserConversation> getAllUserConversations() {
        log.debug("从redis获取所有用户对话列表");
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        Set<String> friendKeys = redisTemplate.keys(CHAT_CONVERSATION_PRIVATE_KEY + "*");
        Set<String> groupKeys = redisTemplate.keys(CHAT_CONVERSATION_GROUP_KEY + "*");
        List<SysUserConversation> result = new ArrayList<>();
        for (String key : friendKeys) {
            result.addAll(hashOperations.values(key));
        }
        for (String key : groupKeys) {
            result.addAll(hashOperations.values(key));
        }
        return result;
    }

    @Override
    public boolean setConversationRead(int userId, int chatId, ChatType chatType) {
        log.debug("在redis中设置对话已读,userId:{},chatId:{},chatType:{}", userId, chatId, chatType);
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        String redisKey;
        if (chatType == ChatType.FRIEND)
            redisKey = CHAT_CONVERSATION_PRIVATE_KEY + userId;
        else
            redisKey = CHAT_CONVERSATION_GROUP_KEY + userId;
        String hashKey = String.valueOf(chatId);
        SysUserConversation sysUserConversation = hashOperations.get(redisKey, hashKey);
        if (sysUserConversation == null) return false;
        sysUserConversation.setUnreadCount(0);
        hashOperations.put(redisKey, hashKey, sysUserConversation);
        return true;
    }

    @Override
    public void addToConversation(List<SysUserConversation> conversation) {
        if (conversation == null || conversation.isEmpty()) return;
        log.debug("在redis中添加用户对话,conversation:{}", conversation);
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        for (SysUserConversation userConversation : conversation) {
            String redisKey = CHAT_CONVERSATION_PRIVATE_KEY + userConversation.getUid();
            if (userConversation.getChatType() == ChatType.GROUP)
                redisKey = CHAT_CONVERSATION_GROUP_KEY + userConversation.getUid();
            String hashKey = String.valueOf(userConversation.getChatId());
            hashOperations.put(redisKey, hashKey, userConversation);
        }
    }

    @Override
    public void deleteConversation(int userId, int senderId, ChatType chatType) {
        log.debug("从redis中删除用户对话,userId:{},senderId:{},chatType:{}", userId, senderId, chatType);
        HashOperations<String, String, SysUserConversation> hashOperations = redisTemplate.opsForHash();
        if (chatType == ChatType.FRIEND)
            hashOperations.delete(CHAT_CONVERSATION_PRIVATE_KEY + userId, String.valueOf(senderId));
        else if (chatType == ChatType.GROUP)
            hashOperations.delete(CHAT_CONVERSATION_GROUP_KEY + userId, String.valueOf(senderId));
    }
}
