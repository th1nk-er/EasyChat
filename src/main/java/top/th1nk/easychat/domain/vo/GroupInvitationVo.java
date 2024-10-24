package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.GroupNotificationType;

import java.time.LocalDateTime;

@Data
@Schema(description = "群组邀请Vo")
public class GroupInvitationVo {
    @Schema(description = "群组ID")
    private Integer groupId;
    @Schema(description = "群组名称")
    private String groupName;
    @Schema(description = "群组头像")
    private String groupAvatar;
    @Schema(description = "邀请人ID")
    private Integer invitedById;
    @Schema(description = "邀请人用户名")
    private Integer invitedByUsername;
    @Schema(description = "邀请人昵称")
    private String invitedByNickname;
    @Schema(description = "邀请人备注")
    private String invitedByRemark;
    @Schema(description = "邀请状态")
    private GroupNotificationType type;
    @Schema(description = "邀请创建时间")
    private LocalDateTime createTime;
}
