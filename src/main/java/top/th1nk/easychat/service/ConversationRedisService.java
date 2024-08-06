package top.th1nk.easychat.service;

import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.SysUserConversation;

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
     * @return 是否设置成功
     */
    boolean setConversationRead(int senderId, int receiverId);

    /**
     * 在redis中添加用户对话
     *
     * @param conversation 用户对话列表
     */
    void addToConversation(List<SysUserConversation> conversation);
}
