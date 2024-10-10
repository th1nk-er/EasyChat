package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.mapper.SysChatMessageMapper;
import top.th1nk.easychat.service.MessageRedisService;
import top.th1nk.easychat.service.SysChatMessageService;

import java.util.Collections;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_chat_message】的数据库操作Service实现
 * @createDate 2024-08-01 18:08:50
 */
@Slf4j
@Service
public class SysChatMessageServiceImpl extends ServiceImpl<SysChatMessageMapper, SysChatMessage>
        implements SysChatMessageService {
    @Resource
    private MessageRedisService messageRedisService;

    /**
     * 分页获取消息
     *
     * @param userId      发送者ID
     * @param chatId      接收者ID
     * @param currentPage 当前页码
     * @return 消息列表
     */
    private List<SysChatMessage> getChatMessageList(int userId, int chatId, ChatType chatType, int currentPage) {
        LambdaQueryWrapper<SysChatMessage> qw = new LambdaQueryWrapper<>();
        if (chatType == ChatType.FRIEND) {
            qw.nested(i -> i.eq(SysChatMessage::getSenderId, userId)
                            .eq(SysChatMessage::getReceiverId, chatId))
                    .or()
                    .nested(i -> i.eq(SysChatMessage::getReceiverId, userId)
                            .eq(SysChatMessage::getSenderId, chatId)
                    )
                    .orderByDesc(SysChatMessage::getCreateTime);
        } else if (chatType == ChatType.GROUP) {
            qw.eq(SysChatMessage::getReceiverId, chatId)
                    .eq(SysChatMessage::getChatType, chatType)
                    .orderByDesc(SysChatMessage::getCreateTime);
        }
        Page<SysChatMessage> sysChatMessagePage = baseMapper.selectPage(new Page<>(currentPage, 15), qw);
        return sysChatMessagePage.getRecords();
    }

    @Override
    public void saveMessage(WSMessage wsMessage) {
        log.debug("持久化存储消息 {}", wsMessage);
        if (messageRedisService.saveMessage(wsMessage) >= 15) {
            List<SysChatMessage> messages = messageRedisService.getMessages(wsMessage.getFromId(), wsMessage.getToId(), wsMessage.getChatType());
            messageRedisService.removeMessage(wsMessage.getFromId(), wsMessage.getToId(), wsMessage.getChatType());
            baseMapper.insert(messages);
        }
    }

    @Override
    public List<SysChatMessage> getFriendMessages(int userId, int chatId, int currentPage) {
        log.debug("分页获取好友消息,userId:{},chatId:{},currentPage:{}", userId, chatId, currentPage);
        if (currentPage < 0 || chatId <= 0) return List.of();
        if (currentPage == 0) {
            // 页码为0时，仅返回redis中聊天记录
            return messageRedisService.getMessages(userId, chatId, ChatType.FRIEND);
        }
        List<SysChatMessage> messages = this.getChatMessageList(userId, chatId, ChatType.FRIEND, currentPage);
        Collections.reverse(messages);
        return messages;
    }

    @Override
    public List<SysChatMessage> getGroupMessages(int groupId, int currentPage) {
        log.debug("分页获取群组消息,groupId:{},currentPage:{}", groupId, currentPage);
        if (currentPage < 0 || groupId <= 0) return List.of();
        if (currentPage == 0) {
            // 页码为0时，仅返回redis中聊天记录
            return messageRedisService.getMessages(-1, groupId, ChatType.GROUP);
        }
        List<SysChatMessage> messages = this.getChatMessageList(-1, groupId, ChatType.GROUP, currentPage);
        Collections.reverse(messages);
        return messages;
    }
}