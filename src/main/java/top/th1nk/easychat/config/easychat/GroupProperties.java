package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "easy-chat.group")
@Configuration
@Data
public class GroupProperties {
    /**
     * 默认群头像
     */
    private String defaultAvatarPath = "classpath:static/img/group-avatar-default.jpg";

    /**
     * 默认头像存储时的文件名
     */
    private String defaultAvatarName = "group-avatar-default.jpg";
    /**
     * minio中头像文件夹名称
     */
    private String avatarDir = "group/avatar";
    /**
     * 每个用户能创建群组数的最大值
     */
    private Integer maxGroupPerUser = 10;
}
