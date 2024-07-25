package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties("easy-chat.web-socket")
@Data
public class WebSocketProperties {
    /**
     * WebSocket端点
     */
    private String endpoint = "/chat/ws";

    /**
     * 应用前缀
     */
    private String appDesPrefix = "/app";

    /**
     * 用户前缀
     */
    private String userDesPrefix = "/user";

    /**
     * 主题
     */
    private List<String> topicPrefix = new ArrayList<>();
}
    