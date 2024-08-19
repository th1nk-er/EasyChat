package top.th1nk.easychat.service;

import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.ChatType;

import java.util.List;

public interface ConversationRedisService {
    /**
     * 当收到消息时，更新用户聊天列表
     *
     * @param message 消息
     */
    void handleMessageReceived(SysChatMessage message);

    /**
     * 从redis中获取用户的聊天列表
     *
     * @param uid 用户ID
     * @return 聊天列表
     */
    List<SysUserConversation> getUserConversations(int uid);

    /**
     * 从redis中获取所有用户的聊天列表
     *
     * @return 聊天列表
     */
    List<SysUserConversation> getAllUserConversations();

    /**
     * 在redis中设置用户对话已读
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @param chatType   聊天类型
     * @return 是否设置成功, redis中不存在指定会话时返回false
     */
    boolean setConversationRead(int senderId, int receiverId, ChatType chatType);

    /**
     * 在redis中添加用户对话
     *
     * @param conversation 用户对话列表
     */
    void addToConversation(List<SysUserConversation> conversation);

    /**
     * 删除某个好友对话
     *
     * @param userId   用户ID
     * @param senderId 发送者ID
     * @param chatType 聊天类型
     */
    void deleteConversation(int userId, int senderId, ChatType chatType);
}
