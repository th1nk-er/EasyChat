package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum GroupInvitationStatus {
    PENDING(0, "待处理"),
    REJECTED(2, "已拒绝"),
    EXPIRED(3, "已过期"),
    ADMIN_PENDING(4, "管理员待处理"),
    ADMIN_ACCEPTED(5, "管理员已同意"),
    ADMIN_REJECTED(6, "管理员已拒绝");

    @EnumValue
    private final int value;
    private final String desc;

    GroupInvitationStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
