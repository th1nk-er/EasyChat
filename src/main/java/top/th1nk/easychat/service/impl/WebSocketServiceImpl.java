package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.JwtProperties;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.service.SysChatMessageService;
import top.th1nk.easychat.service.WebSocketService;
import top.th1nk.easychat.utils.SecurityUtils;
import top.th1nk.easychat.utils.StringUtils;

import java.time.Duration;
import java.util.Set;

@Service
@Slf4j
public class WebSocketServiceImpl implements WebSocketService {
    private static final String WS_PREFIX = "ws:id:";
    @Resource
    private SimpMessagingTemplate simpMessagingTemplate;
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private SysChatMessageService sysChatMessageService;
    @Resource
    private SecurityUtils securityUtils;

    @Override
    public void sendMessage(Authentication authentication, WSMessage message) {
        if (message.getChatType() == ChatType.FRIEND) {
            if (message.getFromId() <= 0) {
                // 系统发送给用户的消息
                return;
            }
            // 用户消息，从redis中取出toId对应的token，
            // 获取其token对应的sha256值，以sha256值为目的地发送ws消息
            if (!securityUtils.getPermissions(message.getFromId()).contains("FRIEND:" + message.getToId()) &&
                    !securityUtils.getPermissions(message.getToId()).contains("FRIEND:" + message.getFromId())) {
                // 非好友关系
                log.warn("用户 {} 试图在非好友状态下向用户 {} 发送消息", message.getFromId(), message.getToId());
                //发送错误提示
                simpMessagingTemplate.convertAndSend("/notify/message/" + StringUtils.getSHA256Hash(authentication.getCredentials().toString()), WSMessage.error(message.getFromId(), "对方不是你的好友"));
                return;
            }
            // 从redis中取出toId对应的token
            Set<String> toTokens = redisTemplate.opsForSet().members(WS_PREFIX + message.getToId());
            if (toTokens != null && !toTokens.isEmpty()) {
                log.info("用户 {} 向用户 {} 发送消息", message.getFromId(), message.getToId());
                for (String toToken : toTokens) {
                    String shaID = StringUtils.getSHA256Hash(toToken);
                    simpMessagingTemplate.convertAndSend("/notify/message/" + shaID, message);
                }
            }
            sysChatMessageService.saveMessage(message);
        } else if (message.getChatType() == ChatType.GROUP) {
            //群组消息
            if (!securityUtils.getPermissions(message.getFromId()).contains("GROUP:" + message.getToId())) {
                // 非群组成员
                log.warn("用户 {} 试图在非群组成员状态下向群组 {} 发送消息", message.getFromId(), message.getToId());
                //发送错误提示
                simpMessagingTemplate.convertAndSend("/notify/message/" + StringUtils.getSHA256Hash(authentication.getCredentials().toString()), WSMessage.error(message.getFromId(), "你不是群组成员"));
                return;
            }
            log.info("用户 {} 向群组 {} 发送消息", message.getFromId(), message.getToId());
            simpMessagingTemplate.convertAndSend("/notify/message/group/" + message.getToId(), message);
        }
    }

    @Override
    public void handleUserConnected(Authentication authentication) {
        log.info("WebSocket User connect:{}", authentication.getPrincipal().toString());
        // 将token存入 redis
        redisTemplate.opsForSet().add(WS_PREFIX + authentication.getPrincipal().toString(), authentication.getCredentials().toString());
        redisTemplate.expire(WS_PREFIX + authentication.getPrincipal().toString(), Duration.ofSeconds(jwtProperties.getExpireSeconds()));
        // 发送已连接消息
        this.sendMessage(authentication, WSMessage.command((Integer) authentication.getPrincipal(), MessageCommand.CONNECTED));
    }

    @Override
    public void sendUserMessage(Authentication authentication, WSMessage message) {
        // 验证message是否合法
        message.setFromId((Integer) authentication.getPrincipal()); // 重新设置一遍fromId，防止恶意修改
        if (!message.isValid()) {
            // message 不合法
            return;
        }
        // 判断发送者用户token是否存储在了redis
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(WS_PREFIX + authentication.getPrincipal().toString(), authentication.getCredentials().toString()))) {
            this.sendMessage(authentication, message);
        } else {
            // 发送者未通过认证
            simpMessagingTemplate.convertAndSend("/notify/message/" + StringUtils.getSHA256Hash(authentication.getCredentials().toString()), WSMessage.error(message.getFromId(), "认证未通过"));
        }
    }

    @Override
    public void handleUserDisconnected(Authentication authentication) {
        log.info("WebSocket User disconnect:{}", authentication.getPrincipal().toString());
        String key = WS_PREFIX + authentication.getPrincipal().toString();
        redisTemplate.opsForSet().remove(key, authentication.getCredentials().toString());
    }
}
