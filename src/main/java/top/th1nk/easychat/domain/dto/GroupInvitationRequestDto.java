package top.th1nk.easychat.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "群聊邀请请求DTO")
@Data
public class GroupInvitationRequestDto {
    @Schema(description = "用户id")
    private Integer userId;
    @Schema(description = "群聊id")
    private int groupId;
    @Schema(description = "是否接受")
    private boolean accept;
}
