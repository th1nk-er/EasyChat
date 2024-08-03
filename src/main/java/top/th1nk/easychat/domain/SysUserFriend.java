package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_user_friend
 */
@TableName(value = "ec_user_friend")
@Data
@Schema(description = "用户好友实体类")
public class SysUserFriend implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer uid;

    /**
     * 好友的用户ID
     */
    @Schema(description = "好友的用户ID")
    private Integer friendId;

    /**
     * 好友备注
     */
    @Schema(description = "好友备注")
    private String remark;

    /**
     * 是否免打扰 0-否 1-是
     */
    @Schema(description = "是否免打扰 0-否 1-是")
    private Boolean muted;
    /**
     * 添加好友的时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "添加好友的时间")
    private LocalDateTime createTime;

    /**
     * 是否已删除 0-未删除 1-已删除
     */
    @Schema(description = "是否已删除 0-未删除 1-已删除")
    private boolean deleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}