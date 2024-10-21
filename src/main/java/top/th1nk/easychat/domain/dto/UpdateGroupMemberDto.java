package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;

@Data
@Schema(description = "更新群组成员信息DTO")
public class UpdateGroupMemberDto {
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "群成员ID")
    private Integer memberId;
    @Schema(description = "用户群昵称")
    private String nickname;
    @Schema(description = "用户身份")
    private UserRole role;
}
