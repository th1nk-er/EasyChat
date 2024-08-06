package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUserConversation;

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
     * @param pageNum 页码
     * @return 聊天列表
     */
    List<SysUserConversation> getChatHistory(int pageNum);

    /**
     * 将对话设为已读
     * 将用户对 receiverId 的对话设为已读
     *
     * @param userId     用户id
     * @param receiverId 接收者id
     */
    void setConversationRead(int userId, int receiverId);

    /**
     * 将用户对话列表加载到redis
     */
    void loadConversationToRedis();
}
