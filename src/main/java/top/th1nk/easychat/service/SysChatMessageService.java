package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.WSMessage;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_chat_message】的数据库操作Service
 * @createDate 2024-08-01 18:08:50
 */
public interface SysChatMessageService extends IService<SysChatMessage> {
    void saveMessage(WSMessage wsMessage);

    /**
     * 分页获取消息
     *
     * @param receiverId  接收者id
     * @param currentPage 当前页码
     * @return 消息列表
     */
    List<SysChatMessage> getMessages(int receiverId, int currentPage);
}