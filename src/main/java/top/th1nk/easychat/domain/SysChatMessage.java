package top.th1nk.easychat.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.domain.chat.ChatType;
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
     * 聊天类型
     */
    @Schema(description = "聊天类型")
    private ChatType chatType;

    /**
     * 接收者ID
     * 当chatType == GROUP 时，接收者ID为群组ID
     */
    @Schema(description = "接收者ID")
    private Integer receiverId;

    /**
     * 消息类型
     */
    @Schema(description = "消息类型")
    private MessageType messageType;

    /**
     * 消息内容
     */
    @Schema(description = "消息内容")
    private String content;
    /**
     * 消息类型为COMMAND时携带的参数
     */
    @Schema(description = "命令参数")
    private String params;

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