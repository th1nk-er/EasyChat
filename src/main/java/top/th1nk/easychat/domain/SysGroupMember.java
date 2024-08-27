package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class SysGroupMember implements Serializable {
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
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户群昵称
     */
    private String userGroupNickname;

    /**
     * 用户给群组的备注
     */
    private String groupRemark;

    /**
     * 用户角色
     */
    private UserRole role;

    /**
     * 加入时间
     */
    private LocalDateTime joinedTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}