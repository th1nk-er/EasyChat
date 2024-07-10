package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.enums.UserSex;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_user
 */
@TableName(value = "ec_user")
@Data
@Schema(description = "用户信息实体类")
public class SysUser implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String username;

    /**
     * 用户昵称
     */
    @Schema(description = "用户昵称")
    private String nickname;

    /**
     * 用户密码
     */
    @Schema(description = "用户密码")
    private String password;

    /**
     * 用户邮箱
     */
    @Schema(description = "用户邮箱")
    private String email;

    /**
     * 头像地址
     */
    @Schema(description = "头像地址")
    private String avatar;

    /**
     * 用户角色 0-普通用户 1-管理员
     */
    @Schema(description = "用户角色 0-普通用户 1-管理员")
    private UserRole role;

    /**
     * 性别 0-男 1-女 2-保密
     */
    @Schema(description = "性别 0-男 1-女 2-保密")
    private UserSex sex;

    /**
     * 注册时IP地址
     */
    @Schema(description = "注册时IP地址")
    private String registerIp;

    /**
     * 登录时IP
     */
    @Schema(description = "登录时IP")
    private String loginIp;

    /**
     * 是否锁定 0-正常 1-锁定
     */
    @Schema(description = "是否锁定 0-正常 1-锁定")
    private Integer locked;

    /**
     * 是否删除 0-正常 1-已删除
     */
    @Schema(description = "是否删除 0-正常 1-已删除")
    private Integer deleted;

    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
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