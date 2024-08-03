package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserSex;

import java.time.LocalDateTime;

@Schema(description = "用户好友vo")
@Data
public class UserFriendVo {
    @Schema(description = "好友用户ID")
    private Integer friendId;
    @Schema(description = "好友用户名")
    private String username;
    @Schema(description = "好友用户性别")
    private UserSex sex;
    @Schema(description = "好友用户昵称")
    private String nickname;
    @Schema(description = "好友用户头像")
    private String avatar;
    @Schema(description = "好友备注")
    private String remark;
    @Schema(description = "是否免打扰")
    private boolean muted;
    @Schema(description = "好友用户创建时间")
    private LocalDateTime createTime;
}
