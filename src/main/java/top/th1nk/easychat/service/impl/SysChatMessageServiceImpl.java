package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.mapper.SysChatMessageMapper;
import top.th1nk.easychat.service.MessageRedisService;
import top.th1nk.easychat.service.SysChatMessageService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.util.ArrayList;
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
    @Resource
    private JwtUtils jwtUtils;

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
    public List<SysChatMessage> getMessages(int receiverId, int currentPage) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        int senderId = userVo.getId();
        List<SysChatMessage> messages = messageRedisService.getMessages(senderId, receiverId);
        List<SysChatMessage> dbMessages = this.getChatMessageList(senderId, receiverId, currentPage);
        Collections.reverse(dbMessages);
        if (dbMessages.isEmpty()) {
            return messages;
        } else {
            if (messages.isEmpty()) {
                return dbMessages;
            } else {
                List<SysChatMessage> result = new ArrayList<>();
                result.addAll(dbMessages);
                result.addAll(messages);
                return result;
            }
        }
    }
}




