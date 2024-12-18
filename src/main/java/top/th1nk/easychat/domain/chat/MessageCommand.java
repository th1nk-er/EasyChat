package top.th1nk.easychat.domain.chat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "消息命令")
@Getter
public enum MessageCommand {
    CONNECTED(1, "CONNECTED"), // 用户建立 ws 连接
    GROUP_INVITED(2, "GROUP_INVITED"), // 用户被邀请加入群组
    MEMBER_MUTED(3, "MEMBER_MUTED"), // 群组成员被禁言
    MEMBER_CANCEL_MUTE(4, "MEMBER_CANCEL_MUTE"), // 群组成员被解除禁言
    GROUP_DISBAND(5, "GROUP_DISBAND"), // 群组解散
    ;
    private final int code;
    private final String desc;

    MessageCommand(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
