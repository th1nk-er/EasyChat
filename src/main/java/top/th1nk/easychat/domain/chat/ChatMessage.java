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
    private String fromId;
    @Schema(description = "接收者id")
    private String toId;
    @Schema(description = "群组id")
    private String groupId;

    public boolean isValid() {
        if (type == null)
            return false;
        else if (content == null || content.isEmpty())
            return false;
        else if (toId == null && groupId == null)
            return false;
        else return fromId != null;
    }

    public static ChatMessage success(String toId) {
        return success(toId, "success");
    }

    public static ChatMessage success(String toId, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.SYSTEM);
        chatMessage.setFromId("");
        chatMessage.setContent(content);
        chatMessage.setToId(toId);
        return chatMessage;
    }

    public static ChatMessage error(String toId) {
        return error(toId, "error");
    }

    public static ChatMessage error(String toId, String errMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.ERROR);
        chatMessage.setFromId(toId);
        chatMessage.setContent(errMsg);
        chatMessage.setToId(toId);
        return chatMessage;
    }

    public static ChatMessage command(String toId, MessageCommand command) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.COMMAND);
        chatMessage.setFromId("");
        chatMessage.setContent(command.getDesc());
        chatMessage.setToId(toId);
        return chatMessage;
    }
}
