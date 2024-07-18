package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.AddUserStatus;
import top.th1nk.easychat.enums.AddUserType;

/**
 * @TableName ec_user_add_friend
 */
@TableName(value = "ec_user_add_friend")
@Data
@Schema(description = "用户添加好友实体类")
public class SysUserAddFriend implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer uid;

    /**
     * 陌生人用户ID
     */
    @Schema(description = "陌生人用户ID")
    private Integer strangerId;

    /**
     * 添加时的附加消息
     */
    @Schema(description = "添加时的附加消息")
    private String addInfo;

    /**
     * 添加时间
     */
    @Schema(description = "添加时间")
    private LocalDateTime createTime;

    /**
     * 处理状态 0-未处理 1-已同意 2-已拒绝 3-已忽略
     */
    @Schema(description = "处理状态 0-未处理 1-已同意 2-已拒绝 3-已忽略")
    private AddUserStatus status;

    /**
     * 添加状态 0-添加对方 1-被对方添加
     */
    @Schema(description = "添加状态 0-添加对方 1-被对方添加")
    private AddUserType addType;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}