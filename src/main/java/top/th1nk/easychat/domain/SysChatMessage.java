package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.domain.chat.MessageType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @TableName ec_chat_message
 */
@TableName(value = "ec_chat_message")
@Data
@Schema(description = "聊天消息")
public class SysChatMessage implements Serializable {
    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 发送者ID
     */
    @Schema(description = "发送者ID")
    private Integer senderId;

    /**
     * 群组消息时，群组ID
     */
    @Schema(description = "群组消息时，群组ID")
    private Integer senderGroupId;

    /**
     * 接收者ID
     */
    @Schema(description = "接收者ID")
    private Integer receiverId;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private MessageType type;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}