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
 * @TableName ec_user_chat_history
 */
@TableName(value = "ec_user_chat_history")
@Data
@Schema(description = "用户聊天列表")
public class SysUserChatHistory implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Integer uid;

    /**
     * 好友ID
     */
    @Schema(description = "好友ID")
    private Integer friendId;

    /**
     * 群组ID
     */
    @Schema(description = "群组ID")
    private Integer groupId;

    /**
     * 未读消息数量
     */
    @Schema(description = "未读消息数量")
    private Integer unreadCount;

    /**
     * 最后一条消息的ID
     */
    @Schema(description = "最后消息的ID")
    private Integer lastMessage;

    /**
     * 最后消息时间
     */
    @Schema(description = "最后消息时间")
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}