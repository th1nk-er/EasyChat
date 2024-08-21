package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * EasyChat配置
 */
@Configuration
@ConfigurationProperties("easy-chat")
@Data
public class EasyChatConfiguration {
    /**
     * Spring Security 配置
     */
    private SecurityProperties security;
    /**
     * jwt配置
     */
    private JwtProperties jwt;

    /**
     * 邮件配置
     */
    private MailProperties mail;

    /**
     * WebSocket配置
     */
    private WebSocketProperties webSocket;

    /**
     * 用户配置
     */
    private UserProperties user;


    /**
     * 聊天配置
     */
    private ChatProperties chat;
}
