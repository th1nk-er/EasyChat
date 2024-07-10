package top.th1nk.easychat.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.enums.UserSex;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "用户信息Vo")
@Data
public class UserVo implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
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
     * 邮箱地址
     */
    @Schema(description = "邮箱地址")
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
     * 是否锁定 0-正常 1-锁定
     */
    @Schema(description = "是否锁定 0-正常 1-锁定")
    private Integer locked;


    /**
     * 注册时间
     */
    @Schema(description = "注册时间")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;
}

