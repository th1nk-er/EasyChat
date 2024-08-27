package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import top.th1nk.easychat.enums.GroupInvitationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_group_invitation
 */
@TableName(value = "ec_group_invitation")
@Data
public class SysGroupInvitation implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 群组ID
     */
    private Integer groupId;

    /**
     * 被邀请用户ID
     */
    private Integer invitedUserId;

    /**
     * 邀请人用户ID
     */
    private Integer invitedBy;

    /**
     * 邀请状态
     */
    private GroupInvitationStatus status;

    /**
     * 邀请创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}