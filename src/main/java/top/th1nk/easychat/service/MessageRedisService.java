package top.th1nk.easychat.service;

import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.WSMessage;

import java.util.List;

public interface MessageRedisService {
    /**
     * 保存消息到redis
     *
     * @param wsMessage 消息
     * @return 保存后列表中消息的数量
     */
    int saveMessage(WSMessage wsMessage);

    /**
     * 从redis中获取指定对话的消息列表
     *
     * @param userId   发送者ID
     * @param chatId   接收者ID
     * @param chatType 聊天类型
     * @return 消息列表
     */
    List<SysChatMessage> getMessages(int userId, int chatId, ChatType chatType);

    /**
     * 删除指定对话的消息缓存
     *
     * @param userId   发送者ID
     * @param chatId   接收者ID
     * @param chatType 聊天类型
     */
    void removeMessage(int userId, int chatId, ChatType chatType);

    /**
     * 获取redis中所有对话的消息
     *
     * @return 消息列表
     */
    List<SysChatMessage> getAllMessages();
}
