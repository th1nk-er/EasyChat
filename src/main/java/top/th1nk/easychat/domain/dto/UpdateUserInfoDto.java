package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserSex;

@Data
@Schema(description = "更新用户信息DTO")
public class UpdateUserInfoDto {
    @Schema(description = "昵称")
    private String nickname;
    @Schema(description = "性别")
    private UserSex sex;
}
