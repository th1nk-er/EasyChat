package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.JwtProperties;
import top.th1nk.easychat.domain.chat.ChatMessage;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.service.WebSocketService;
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

    @Override
    public void sendMessage(ChatMessage message) {
        if (message.getToId() != null) {
            // 用户消息，从redis中取出toId对应的token，
            // 获取其token对应的sha256值，以sha256值为目的地发送ws消息
            //TODO 当toId不为空时判断双方是否为好友关系
            // 从redis中取出toId对应的token
            Set<String> toTokens = redisTemplate.opsForSet().members(WS_PREFIX + message.getToId());
            if (toTokens == null || toTokens.isEmpty()) {
                //TODO 对方不在线发送离线消息
            } else {
                log.info("用户 {} 向用户 {} 发送消息", message.getFromId(), message.getToId());
                // 对方有在线设备
                for (String toToken : toTokens) {
                    // 对每一个设备发送消息
                    String shaID = StringUtils.getSHA256Hash(toToken);
                    simpMessagingTemplate.convertAndSend("/notify/message/" + shaID, message);
                }
            }
        } else if (message.getGroupId() != null) {
            //群组消息
            //TODO 当groupId不为空时判断用户是否为群组成员
            return;
        }
    }

    @Override
    public void handleUserConnected(Authentication authentication) {
        log.info("WebSocket User connect:{}", authentication.getPrincipal().toString());
        // 将token存入 redis
        redisTemplate.opsForSet().add(WS_PREFIX + authentication.getPrincipal().toString(), authentication.getCredentials().toString());
        redisTemplate.expire(WS_PREFIX + authentication.getPrincipal().toString(), Duration.ofSeconds(jwtProperties.getExpireSeconds()));
        // 发送已连接消息
        this.sendMessage(ChatMessage.command((Integer) authentication.getPrincipal(), MessageCommand.CONNECTED));
    }

    @Override
    public void sendUserMessage(Authentication authentication, ChatMessage message) {
        // 验证message是否合法
        message.setFromId((Integer) authentication.getPrincipal()); // 重新设置一遍fromId，防止恶意修改
        if (!message.isValid()) {
            // message 不合法
            return;
        }
        // 判断发送者用户token是否存储在了redis
        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(WS_PREFIX + authentication.getPrincipal().toString(), authentication.getCredentials().toString()))) {
            this.sendMessage(message);
        } else {
            // 发送者未通过认证
            return;
        }
    }

    @Override
    public void handleUserDisconnected(Authentication authentication) {
        log.info("WebSocket User disconnect:{}", authentication.getPrincipal().toString());
        String key = WS_PREFIX + authentication.getPrincipal().toString();
        redisTemplate.opsForSet().remove(key, authentication.getCredentials().toString());
    }
}
