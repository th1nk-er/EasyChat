package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户Token Vo")
public class UserTokenVo {
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "设备UA")
    private String userAgent;
    @Schema(description = "登录IP")
    private String loginIp;
    @Schema(description = "签发时间")
    private LocalDateTime issueTime;
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;
}
