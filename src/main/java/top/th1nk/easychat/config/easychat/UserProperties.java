package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("easy-chat.user")
@Data
public class UserProperties {
    /**
     * 默认头像
     */
    private String defaultAvatarPath = "classpath:static/img/avatar-default.jpg";
    /**
     * 默认头像存储时的文件名
     */
    private String defaultAvatarName = "default.jpg";

    /**
     * 最大头像大小，单位：KB
     */
    private Integer avatarMaxSize = 1024 * 5;

    /**
     * minio中头像文件夹名称
     */
    private String avatarDir = "avatar";
}
