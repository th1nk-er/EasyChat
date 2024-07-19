package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户发送的消息")
public class ChatMessage {
    @Schema(description = "消息类型")
    private MessageType type;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "发送者id")
    private int fromId;
    @Schema(description = "接收者id")
    private int toId;
}
