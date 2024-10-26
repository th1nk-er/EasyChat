package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.GroupNotificationType;

import java.time.LocalDateTime;

@Data
@Schema(description = "群聊通知Vo")
public class GroupNotificationVo {
    @Schema(description = "群聊ID")
    private Integer groupId;
    @Schema(description = "群聊名称")
    private String groupName;
    @Schema(description = "群聊头像")
    private String groupAvatar;
    @Schema(description = "目标用户ID")
    private Integer targetId;
    @Schema(description = "目标用户名")
    private String targetUsername;
    @Schema(description = "目标用户昵称")
    private String targetNickname;
    @Schema(description = "操作用户ID")
    private Integer operatorId;
    @Schema(description = "操作用户名")
    private Integer operatorUsername;
    @Schema(description = "操作用户昵称")
    private String operatorNickname;
    @Schema(description = "通知类型")
    private GroupNotificationType type;
    @Schema(description = "通知创建时间")
    private LocalDateTime createTime;
}
