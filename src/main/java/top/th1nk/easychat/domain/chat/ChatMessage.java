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
    private Integer fromId;
    @Schema(description = "接收者id")
    private Integer toId;
    @Schema(description = "群组id")
    private Integer groupId;

    public boolean isValid() {
        if (type == null)
            return false;
        else if (content == null || content.isEmpty())
            return false;
        else if (toId == null && groupId == null)
            return false;
        else return fromId != null;
    }

    public static ChatMessage success(Integer toId) {
        return success(toId, "success");
    }

    public static ChatMessage success(Integer toId, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.SYSTEM);
        chatMessage.setFromId(-1);
        chatMessage.setContent(content);
        chatMessage.setToId(toId);
        return chatMessage;
    }

    public static ChatMessage error(Integer toId) {
        return error(toId, "error");
    }

    public static ChatMessage error(Integer toId, String errMsg) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.ERROR);
        chatMessage.setFromId(-1);
        chatMessage.setContent(errMsg);
        chatMessage.setToId(toId);
        return chatMessage;
    }

    public static ChatMessage command(Integer toId, MessageCommand command) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setType(MessageType.COMMAND);
        chatMessage.setFromId(-1);
        chatMessage.setContent(command.getDesc());
        chatMessage.setToId(toId);
        return chatMessage;
    }
}
