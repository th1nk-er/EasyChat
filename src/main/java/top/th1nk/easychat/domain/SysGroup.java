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
 * @TableName ec_group
 */
@TableName(value = "ec_group")
@Data
@Schema(description = "群组")
public class SysGroup implements Serializable {
    /**
     * 群组ID
     */
    @TableId(type = IdType.AUTO)
    @Schema(description = "群组ID")
    private Integer groupId;

    /**
     * 群组名称
     */
    @Schema(description = "群组名称")
    private String groupName;

    /**
     * 群组描述信息
     */
    @Schema(description = "群组描述信息")
    private String groupDesc;

    /**
     * 群组头像
     */
    @Schema(description = "群组头像")
    private String avatar;

    /**
     * 是否已删除
     */
    @Schema(description = "是否已删除")
    private Integer deleted;

    /**
     * 群组创建时间
     */
    @Schema(description = "群组创建时间")
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    @Schema(description = "最后更新时间")
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}