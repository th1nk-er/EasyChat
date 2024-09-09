package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.GroupInvitationStatus;

import java.time.LocalDateTime;

@Data
@Schema(description = "管理员的群组邀请Vo")
public class GroupAdminInvitationVo {
    @Schema(description = "群组ID")
    private Integer groupId;
    @Schema(description = "群组名称")
    private String groupName;
    @Schema(description = "群组头像")
    private String groupAvatar;
    @Schema(description = "邀请人id")
    private Integer invitedById;
    @Schema(description = "邀请人用户名")
    private Integer invitedByUsername;
    @Schema(description = "邀请人昵称")
    private String invitedByNickname;
    @Schema(description = "受邀请人ID")
    private Integer inviterId;
    @Schema(description = "受邀请人用户名")
    private String inviterUsername;
    @Schema(description = "受邀请人昵称")
    private String inviterNickname;
    @Schema(description = "邀请状态")
    private GroupInvitationStatus status;
    @Schema(description = "邀请创建时间")
    private LocalDateTime createTime;
}
