package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import top.th1nk.easychat.domain.SysUserConversation;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户聊天列表Vo")
public class UserConversationVo extends SysUserConversation {
    @Schema(description = "好友/群组名称")
    private String nickname;
    @Schema(description = "好友/群组头像")
    private String avatar;
    @Schema(description = "好友/群组备注")
    private String remark;
    @Schema(description = "是否静音")
    private boolean muted;
}
