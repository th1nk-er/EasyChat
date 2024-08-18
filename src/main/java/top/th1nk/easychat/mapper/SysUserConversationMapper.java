package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUserConversation;

/**
 * @author vinka
 * @description 针对表【ec_user_chat_history】的数据库操作Mapper
 * @createDate 2024-08-01 18:08:50
 * @Entity top.th1nk.easychat.domain.SysUserChatHistory
 */
public interface SysUserConversationMapper extends BaseMapper<SysUserConversation> {
    /**
     * 根据uid和friendId删除会话
     *
     * @param uid      用户id
     * @param friendId 好友id
     * @return 影响的行数
     */
    int deleteByUidAndFriendId(Integer uid, Integer friendId);
}




