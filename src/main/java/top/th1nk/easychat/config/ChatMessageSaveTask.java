package top.th1nk.easychat.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.mapper.SysChatMessageMapper;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.MessageRedisService;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
@Component
public class ChatMessageSaveTask {
    @Resource
    private SysChatMessageMapper sysChatMessageMapper;
    @Resource
    private SysUserConversationMapper sysUserConversationMapper;
    @Resource
    private MessageRedisService messageRedisService;
    @Resource
    private ConversationRedisService conversationRedisService;

    @Scheduled(cron = "0 0 * * * ?")
    @EventListener(ContextClosedEvent.class)
    public void chatMessageSave() {
        List<SysChatMessage> allMessages = messageRedisService.getAllMessages();
        sysChatMessageMapper.insert(allMessages);
        // 清除redis中的对话消息
        for (SysChatMessage allMessage : allMessages) {
            messageRedisService.removeMessage(allMessage.getSenderId(), allMessage.getReceiverId());
        }
        log.info("定时任务：保存[{}]条聊天记录", allMessages.size());
    }

    @Scheduled(cron = "0 0 * * * ?")
    @EventListener(ContextClosedEvent.class)
    public void userConversationSave() {
        List<SysUserConversation> userConversations = conversationRedisService.getAllUserConversations();
        for (SysUserConversation conversation : userConversations) {
            LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
            qw.eq(SysUserConversation::getUid, conversation.getUid())
                    .eq(SysUserConversation::getSenderId, conversation.getSenderId());
            SysUserConversation userConversation = sysUserConversationMapper.selectOne(qw);
            if (userConversation == null)
                sysUserConversationMapper.insert(conversation);
            else {
                userConversation.setUnreadCount(conversation.getUnreadCount());
                userConversation.setMessageType(conversation.getMessageType());
                userConversation.setLastMessage(conversation.getLastMessage());
                userConversation.setUpdateTime(conversation.getUpdateTime());
                sysUserConversationMapper.updateById(userConversation);
            }

        }
        log.info("定时任务：保存[{}]条用户对话", userConversations.size());
    }
}