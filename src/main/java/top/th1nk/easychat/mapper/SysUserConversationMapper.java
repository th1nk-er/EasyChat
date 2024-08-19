package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.ChatType;

/**
 * @author vinka
 * @description 针对表【ec_user_chat_history】的数据库操作Mapper
 * @createDate 2024-08-01 18:08:50
 * @Entity top.th1nk.easychat.domain.SysUserChatHistory
 */
public interface SysUserConversationMapper extends BaseMapper<SysUserConversation> {
    /**
     * 根据uid和senderId删除会话
     *
     * @param uid      用户id
     * @param senderId 接收者id
     * @return 影响的行数
     */
    int deleteConversation(Integer uid, Integer senderId, ChatType chatType);
}




