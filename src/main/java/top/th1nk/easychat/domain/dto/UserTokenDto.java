package top.th1nk.easychat.domain.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserTokenDto implements Serializable {
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
