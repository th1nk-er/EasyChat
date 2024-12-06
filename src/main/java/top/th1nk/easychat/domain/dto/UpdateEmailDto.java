package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "修改邮箱信息")
@Data
public class UpdateEmailDto {
    @Schema(description = "用户ID")
    private int userId;
    @Schema(description = "邮箱验证码")
    private String code;
    @Schema(description = "新邮箱")
    private String newEmail;
    @Schema(description = "新邮箱验证码")
    private String newCode;
}
