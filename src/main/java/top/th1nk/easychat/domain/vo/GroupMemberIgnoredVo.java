package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "群成员屏蔽Vo")
public class GroupMemberIgnoredVo {
    @Schema(description = "群聊ID")
    private Integer groupId;
    @Schema(description = "用户ID")
    private Integer userId;
    @Schema(description = "屏蔽的用户ID")
    private Integer ignoredId;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
