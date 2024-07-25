package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("easy-chat.jwt")
@Data
public class JwtProperties {
    /**
     * 密钥
     */
    private String secret;

    /**
     * 有效时长 单位：秒
     */
    private long expireSeconds = 3600;

    /**
     * 用户最大Token数量
     * 用于限制用户登录设备数目
     */
    private int maxToken = 3;
}