package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.GroupNotificationType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_group_invitation
 */
@TableName(value = "ec_group_notification")
@Data
@Schema(description = "群聊通知")
public class SysGroupNotification implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 群组ID
     */
    @Schema(description = "群组ID")
    private Integer groupId;

    /**
     * 被邀请用户ID
     */
    @Schema(description = "目标用户ID")
    private Integer targetId;

    /**
     * 邀请人用户ID
     */
    @Schema(description = "操作用户ID")
    private Integer operatorId;

    /**
     * 邀请状态
     */
    @Schema(description = "通知状态")
    private GroupNotificationType type;

    /**
     * 邀请创建时间
     */
    @Schema(description = "邀请创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}