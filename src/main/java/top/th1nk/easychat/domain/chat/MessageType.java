package top.th1nk.easychat.domain.chat;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum MessageType {
    TEXT(0, "文本"),
    IMAGE(1, "图片"),
    FILE(2, "文件"),
    SYSTEM(3, "系统"),
    ERROR(4, "错误"),
    COMMAND(5, "命令"),
    ;
    @EnumValue
    private final int code;
    private final String desc;

    MessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
