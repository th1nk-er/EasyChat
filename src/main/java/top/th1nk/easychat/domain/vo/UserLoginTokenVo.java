package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户登录后下发的Token Vo")
public class UserLoginTokenVo implements Serializable {
    @Schema(description = "token ID")
    private int id;
    @Schema(description = "用户Token")
    private String token;
    @Schema(description = "签发时间")
    private LocalDateTime issueTime;
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;
}
