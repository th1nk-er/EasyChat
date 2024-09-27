package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "WebSocket消息")
public class WSMessage {
    @Schema(description = "消息类型")
    private MessageType messageType;
    @Schema(description = "消息内容")
    private String content;
    @Schema(description = "发送者id")
    private Integer fromId;
    @Schema(description = "接收者id")
    private Integer toId;
    @Schema(description = "聊天类型")
    private ChatType chatType;
    @Schema(description = "消息类型为COMMAND时携带的参数")
    private List<String> params;

    public boolean isValid() {
        return messageType != null && chatType != null && content != null && !content.isEmpty() && fromId != null && toId != null;
    }

    public static WSMessage success(Integer toId) {
        return success(toId, "success");
    }

    public static WSMessage success(Integer toId, String content) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setMessageType(MessageType.SYSTEM);
        wsMessage.setFromId(-1);
        wsMessage.setContent(content);
        wsMessage.setToId(toId);
        return wsMessage;
    }

    public static WSMessage error(Integer toId) {
        return error(toId, "error");
    }

    public static WSMessage error(Integer toId, String errMsg) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setMessageType(MessageType.ERROR);
        wsMessage.setFromId(-1);
        wsMessage.setContent(errMsg);
        wsMessage.setToId(toId);
        return wsMessage;
    }

    public static WSMessage command(Integer toId, MessageCommand command) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setMessageType(MessageType.COMMAND);
        wsMessage.setFromId(-1);
        wsMessage.setContent(command.getDesc());
        wsMessage.setToId(toId);
        return wsMessage;
    }
}
