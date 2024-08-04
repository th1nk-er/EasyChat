package top.th1nk.easychat.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import top.th1nk.easychat.domain.SysChatMessage;
import top.th1nk.easychat.mapper.SysChatMessageMapper;
import top.th1nk.easychat.service.MessageRedisService;

import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
public class ChatMessageSaveTask {
    @Resource
    private SysChatMessageMapper sysChatMessageMapper;
    @Resource
    private MessageRedisService messageRedisService;

    @Scheduled(cron = "0 0 * * * ?")
    public void chatMessageSave() {
        List<SysChatMessage> allMessages = messageRedisService.getAllMessages();
        sysChatMessageMapper.insert(allMessages);
    }
}