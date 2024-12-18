package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.vo.UserConversationVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_conversation】的数据库操作Service
 * @createDate 2024-08-01 18:08:50
 */
public interface SysUserConversationService extends IService<SysUserConversation> {
    /**
     * 获取用户对话列表
     *
     * @param userId 用户id
     * @return 聊天列表
     */
    List<UserConversationVo> getUserConversations(int userId);

    /**
     * 将对话设为已读
     * 将用户对 chatId 的对话设为已读
     *
     * @param userId   用户id
     * @param chatId   对方id
     * @param chatType 聊天类型
     */
    void setConversationRead(int userId, int chatId, ChatType chatType);
}
