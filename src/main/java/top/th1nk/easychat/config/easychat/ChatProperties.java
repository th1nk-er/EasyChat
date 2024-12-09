package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("easy-chat.chat")
@Data
public class ChatProperties {
    /**
     * 图片最大尺寸
     */
    private long imageMaxSize = 1024 * 1024 * 5;
    /**
     * 文件最大尺寸
     */
    private long fileMaxSize = 1024 * 1024 * 200;

    /**
     * 聊天中文件存储时路径前缀
     */
    private String fileDir = "chat-file";
}
