package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.enums.UserSex;

import java.time.LocalDateTime;

@Data
@Schema(description = "群成员信息Vo")
public class GroupMemberInfoVo {
    @Schema(description = "群聊ID")
    private Integer groupId;
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "用户群昵称")
    private String userGroupNickname;
    @Schema(description = "用户角色")
    private UserRole role;
    @Schema(description = "加入时间")
    private LocalDateTime createTime;
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "用户昵称")
    private String nickname;
    @Schema(description = "用户头像")
    private String avatar;
    @Schema(description = "用户性别")
    private UserSex sex;
}
