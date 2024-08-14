package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("easy-chat.mail")
@Data
public class MailProperties {
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

            /**
             * 操作名称占位符
             */
            private String actionPlaceholder = "{ACTION}";
        }
    }
}