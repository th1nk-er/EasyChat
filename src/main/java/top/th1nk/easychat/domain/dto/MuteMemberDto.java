package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "禁言群成员Dto")
public class MuteMemberDto {
    @Schema(description = "群聊ID")
    private int groupId;
    @Schema(description = "群成员ID")
    private int memberId;
    @Schema(description = "执行禁言的管理员ID")
    private int adminId;
    @Schema(description = "禁言时长，单位：分钟")
    private int duration;
}
