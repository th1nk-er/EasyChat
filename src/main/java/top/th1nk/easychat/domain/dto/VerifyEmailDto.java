package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "邮箱验证")
public class VerifyEmailDto {
    @Schema(description = "邮箱")
    private String email;
}
