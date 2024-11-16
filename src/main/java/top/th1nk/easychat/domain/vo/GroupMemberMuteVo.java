package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "群成员禁言Vo")
public class GroupMemberMuteVo {
    @Schema(description = "群聊ID")
    private int groupId;
    @Schema(description = "群成员ID")
    private int userId;
    @Schema(description = "执行禁言的管理员ID")
    private int adminId;
    @Schema(description = "是否被禁言")
    private boolean muted;
    @Schema(description = "禁言起始时间")
    private LocalDateTime muteTime;
    @Schema(description = "禁言结束时间")
    private LocalDateTime unmuteTime;
}
