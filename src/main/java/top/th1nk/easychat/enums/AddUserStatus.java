package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum AddUserStatus {
    PENDING(0, "待处理"),
    AGREED(1, "已同意"),
    REFUSED(2, "已拒绝"),
    IGNORED(3, "已忽略");

    @EnumValue
    private final int code;
    private final String desc;

    AddUserStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
