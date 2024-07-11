package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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
    private Security security;
    /**
     * jwt配置
     */
    private Jwt jwt;

    /**
     * 邮件配置
     */
    private Mail mail;

    @Data
    public static class Security {
        /**
         * 放行路径
         */
        private List<String> permitAllUrls = null;

        /**
         * 需要认证的路径
         */
        private List<String> authorizeUrls = null;
    }

    @Data
    public static class Jwt {
        /**
         * 密钥
         */
        private String secret;

        /**
         * 有效时长 单位：秒
         */
        private long expireSeconds = 3600;
    }

    @Data
    public static class Mail {
        /**
         * 发送者配置
         */
        private Sender sender;
        /**
         * 验证码配置
         */
        private VerifyCode verifyCode;

        @Data
        public static class Sender {
            /**
             * 发送者名称
             */
            private String name = "EasyChat";
        }

        @Data
        public static class VerifyCode {
            /**
             * 有效时长 单位：分钟
             */
            private int expire = 15;

            /**
             * 模板
             */
            private Template template;

            @Data
            public static class Template {
                /**
                 * 模板路径
                 */
                private String path;

                /**
                 * 应用名称占位符
                 */
                private String applicationNamePlaceholder = "{APPLICATION_NAME}";

                /**
                 * 验证码占位符
                 */
                private String codePlaceholder = "{VERIFY_CODE}";

                /**
                 * 有效期占位符
                 */
                private String expirePlaceholder = "{EXPIRE_TIME}";
            }
        }
    }
}
