package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.LoginType;

@Data
@Schema(description = "登录信息")
public class LoginDto {
    @Schema(description = "登录类型")
    private LoginType type;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "邮箱")
    private String email;
    @Schema(description = "验证码")
    private String verifyCode;
}
