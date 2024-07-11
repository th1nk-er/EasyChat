package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_user_token
 */
@TableName(value = "ec_user_token")
@Data
@Schema(description = "用户Token")
public class SysUserToken implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer userId;
    /**
     * 设备UA
     */
    @Schema(description = "设备UA")
    private String userAgent;

    /**
     * 登录IP
     */
    @Schema(description = "登录IP")
    private String loginIp;
    /**
     * 用户Token
     */
    @Schema(description = "用户Token")
    private String token;

    /**
     * 签发时间
     */
    @Schema(description = "签发时间")
    private LocalDateTime issueTime;

    /**
     * 到期时间
     */
    @Schema(description = "到期时间")
    private LocalDateTime expireTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}