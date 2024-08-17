package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户好友更新dto")
public class UserFriendUpdateDto {
    @Schema(description = "好友用户ID")
    private int friendId;
    @Schema(description = "好友备注")
    private String remark;
    @Schema(description = "是否免打扰")
    private boolean muted;
}
