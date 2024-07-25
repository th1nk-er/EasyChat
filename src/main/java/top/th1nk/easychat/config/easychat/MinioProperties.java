package top.th1nk.easychat.config.easychat;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("easy-chat.minio")
@Data
public class MinioProperties {
    /**
     * 访问地址
     */
    private String endpoint = "http://127.0.0.1:9000";
    /**
     * 用户名
     */
    private String accessKey = "minioadmin";
    /**
     * 密码
     */
    private String secretKey = "miniopassword";

    /**
     * 存储桶名称
     */
    private String bucketName = "bucket-ec";
}
