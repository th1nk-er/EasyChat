package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改密码信息")
public class UpdatePasswordDto {
    @Schema(description = "用户ID")
    private int userId;
    @Schema(description = "邮箱验证码")
    private String code;
    @Schema(description = "新密码")
    private String newPassword;
}
