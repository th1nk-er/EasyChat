package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "消息命令")
@Getter
public enum MessageCommand {
    CONNECTED(1, "CONNECTED"),
    ;
    private final int code;
    private final String desc;

    MessageCommand(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
