package top.th1nk.easychat.service.impl;

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

    @Override
    public void saveMessage(WSMessage wsMessage) {
        if (messageRedisService.saveMessage(wsMessage) >= 15) {
            int receiverId = wsMessage.getToId() == null ? wsMessage.getGroupId() : wsMessage.getToId();
            List<SysChatMessage> messages = messageRedisService.getMessages(wsMessage.getFromId(), receiverId);
            messageRedisService.removeMessage(wsMessage.getFromId(), receiverId);
            baseMapper.insert(messages);
        }
    }

    @Override
    public List<SysChatMessage> getMessages(int receiverId, int currentPage) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        int senderId = userVo.getId();
        List<SysChatMessage> messages = messageRedisService.getMessages(senderId, receiverId);
        List<SysChatMessage> dbMessages = baseMapper.getChatMessageList(senderId, receiverId, currentPage, 15);
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




