package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysChatMessage;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_chat_message】的数据库操作Mapper
 * @createDate 2024-08-01 18:08:50
 * @Entity top.th1nk.easychat.domain.SysChatMessage
 */
public interface SysChatMessageMapper extends BaseMapper<SysChatMessage> {
    /**
     * 获取两个用户之间的消息
     * @param uid1 用户1的ID
     * @param uid2 用户2的ID
     * @param currentPage 当前页码
     * @param pageSize 每页显示的消息数量
     * @return 消息列表
     */
    List<SysChatMessage> getChatMessageList(int uid1, int uid2, int currentPage, int pageSize);
}




