package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum GroupStatus {
    NORMAL(0, "正常"),
    DISBAND(1, "解散"),
    ;
    @EnumValue
    private final int value;
    private final String desc;

    GroupStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
