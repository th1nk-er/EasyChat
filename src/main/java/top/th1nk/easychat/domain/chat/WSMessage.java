package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "WebSocket消息")
public class WSMessage {
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

    public static WSMessage success(Integer toId) {
        return success(toId, "success");
    }

    public static WSMessage success(Integer toId, String content) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setType(MessageType.SYSTEM);
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
        wsMessage.setType(MessageType.ERROR);
        wsMessage.setFromId(-1);
        wsMessage.setContent(errMsg);
        wsMessage.setToId(toId);
        return wsMessage;
    }

    public static WSMessage command(Integer toId, MessageCommand command) {
        WSMessage wsMessage = new WSMessage();
        wsMessage.setType(MessageType.COMMAND);
        wsMessage.setFromId(-1);
        wsMessage.setContent(command.getDesc());
        wsMessage.setToId(toId);
        return wsMessage;
    }
}
