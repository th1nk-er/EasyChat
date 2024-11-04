package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_group_member_muted
 */
@TableName(value = "ec_group_member_muted")
@Data
public class SysGroupMemberMuted implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 群聊ID
     */
    private Integer groupId;

    /**
     * 是否禁言
     */
    private boolean muted;

    /**
     * 禁言起始时间
     */
    private LocalDateTime muteTime;

    /**
     * 禁言结束时间
     */
    private LocalDateTime unmuteTime;

    /**
     * 执行禁言的管理员ID
     */
    private Integer adminId;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}