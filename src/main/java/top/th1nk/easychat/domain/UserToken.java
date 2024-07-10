package top.th1nk.easychat.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户Token")
public class UserToken {
    @Schema(description = "Token字符串")
    private String token;
    @Schema(description = "签发时间")
    private LocalDateTime issueTime;
    @Schema(description = "过期时间")
    private LocalDateTime expireTime;
}
