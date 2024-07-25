package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("easy-chat.security")
@Data
public class SecurityProperties {
    /**
     * 放行路径
     */
    private List<String> permitAllUrls = null;

    /**
     * 需要认证的路径
     */
    private List<String> authorizeUrls = null;
}
