package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_group_member
 */
@TableName(value = "ec_group_member")
@Data
@Schema(description = "群组成员")
public class SysGroupMember implements Serializable {
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
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;

    /**
     * 用户群昵称
     */
    @Schema(description = "用户群昵称")
    private String userGroupNickname;

    /**
     * 用户给群组的备注
     */
    @Schema(description = "用户给群组的备注")
    private String groupRemark;

    /**
     * 用户角色
     */
    @Schema(description = "用户角色")
    private UserRole role;


    /**
     * 是否已删除 0-否 1-是
     */
    @Schema(description = "是否已删除 0-否 1-是")
    private boolean deleted;

    /**
     * 是否免打扰 0-否 1-是
     */
    @Schema(description = "是否免打扰 0-否 1-是")
    private boolean muted;
    /**
     * 加入时间
     */
    @Schema(description = "加入时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}