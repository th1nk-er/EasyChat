package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
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
@Service
public class SysChatMessageServiceImpl extends ServiceImpl<SysChatMessageMapper, SysChatMessage>
        implements SysChatMessageService {
    @Resource
    private MessageRedisService messageRedisService;

    /**
     * 分页获取消息
     *
     * @param senderId    发送者ID
     * @param receiverId  接收者ID
     * @param currentPage 当前页码
     * @return 消息列表
     */
    private List<SysChatMessage> getChatMessageList(int senderId, int receiverId, int currentPage) {
        LambdaQueryWrapper<SysChatMessage> qw = new LambdaQueryWrapper<>();
        qw.nested(i -> i.eq(SysChatMessage::getSenderId, senderId)
                        .eq(SysChatMessage::getReceiverId, receiverId))
                .or()
                .nested(i -> i.eq(SysChatMessage::getReceiverId, senderId)
                        .eq(SysChatMessage::getSenderId, receiverId)
                )
                .orderByDesc(SysChatMessage::getCreateTime);
        Page<SysChatMessage> sysChatMessagePage = baseMapper.selectPage(new Page<>(currentPage, 15), qw);
        return sysChatMessagePage.getRecords();
    }

    @Override
    public void saveMessage(WSMessage wsMessage) {
        if (messageRedisService.saveMessage(wsMessage) >= 15) {
            List<SysChatMessage> messages = messageRedisService.getMessages(wsMessage.getFromId(), wsMessage.getToId());
            messageRedisService.removeMessage(wsMessage.getFromId(), wsMessage.getToId());
            baseMapper.insert(messages);
        }
    }

    @Override
    public List<SysChatMessage> getMessages(int userId, int receiverId, int currentPage) {
        if (currentPage < 0 || receiverId <= 0) return List.of();
        if (currentPage == 0) {
            // 页码为0时，仅返回redis中聊天记录
            return messageRedisService.getMessages(userId, receiverId);

        }
        List<SysChatMessage> messages = this.getChatMessageList(userId, receiverId, currentPage);
        Collections.reverse(messages);
        return messages;
    }
}