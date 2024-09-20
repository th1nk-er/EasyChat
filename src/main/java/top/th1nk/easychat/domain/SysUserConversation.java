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
 * @TableName ec_user_conversation
 */
@TableName(value = "ec_user_conversation")
@Data
@Schema(description = "用户聊天列表")
public class SysUserConversation implements Serializable {
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
     * 发送者ID
     */
    @Schema(description = "对方ID")
    private Integer chatId;

    /**
     * 聊天类型
     */
    @Schema(description = "群组ID")
    private ChatType chatType;

    /**
     * 未读消息数量
     */
    @Schema(description = "未读消息数量")
    private Integer unreadCount;

    /**
     * 最后一条消息的ID
     */
    @Schema(description = "最后消息内容")
    private String lastMessage;

    /**
     * 最后一条消息的类型
     */
    @Schema(description = "最后消息类型")
    private MessageType messageType;

    /**
     * 最后消息时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "最后消息时间")
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}