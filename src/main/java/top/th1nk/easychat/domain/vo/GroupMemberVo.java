package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;

import java.time.LocalDateTime;

@Data
@Schema(description = "群成员Vo")
public class GroupMemberVo {
    @Schema(description = "群聊ID")
    private Integer groupId;
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "用户群昵称")
    private String userGroupNickname;
    @Schema(description = "用户给群组的备注")
    private String groupRemark;
    @Schema(description = "用户角色")
    private UserRole role;
    @Schema(description = "加入时间")
    private LocalDateTime createTime;
}
