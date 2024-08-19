package top.th1nk.easychat.domain.chat;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ChatType {
    FRIEND(0, "好友"),
    GROUP(1, "群组"),
    ;
    @EnumValue
    private final int value;
    private final String desc;

    ChatType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
