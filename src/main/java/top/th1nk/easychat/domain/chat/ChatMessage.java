package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import top.th1nk.easychat.utils.StringUtils;

@Data
@Schema(description = "用户发送的消息")
public class ChatMessage {
    @Schema(description = "消息类型")
    private MessageType type;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "发送者id")
    private String fromId;
    @Schema(description = "接收者id")
    private String toId;

    public static ChatMessage success(String tokenString) {
        return success(tokenString, "success");
    }

    public static ChatMessage success(String tokenString, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.SYSTEM);
        chatMessage.setFromId("");
        chatMessage.setContent(content);
        chatMessage.setToId(StringUtils.getSHA256Hash(tokenString));
        return chatMessage;
    }

    public static ChatMessage error(String tokenString) {
        return error(tokenString, "error");
    }

    public static ChatMessage error(String tokenString, String errMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.ERROR);
        chatMessage.setFromId("");
        chatMessage.setContent(errMsg);
        chatMessage.setToId(StringUtils.getSHA256Hash(tokenString));
        return chatMessage;
    }

    public static ChatMessage command(String tokenString, MessageCommand command) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.COMMAND);
        chatMessage.setFromId("");
        chatMessage.setContent(command.getDesc());
        chatMessage.setToId(StringUtils.getSHA256Hash(tokenString));
        return chatMessage;
    }
}
