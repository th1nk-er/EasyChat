package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "验证码信息")
public class EmailVerifyDto {
    @Schema(description = "原始邮箱验证码")
    private String code;
    @Schema(description = "验证的邮箱")
    private String email;
}
