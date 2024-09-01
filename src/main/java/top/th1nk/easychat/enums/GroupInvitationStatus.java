package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum GroupInvitationStatus {
    PENDING(0, "待处理"),
    ACCEPTED(1, "已同意"),
    REJECTED(2, "已拒绝"),
    EXPIRED(3, "已过期");

    @EnumValue
    private final int value;
    private final String desc;

    GroupInvitationStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
