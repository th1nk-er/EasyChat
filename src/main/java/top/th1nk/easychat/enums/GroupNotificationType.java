package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum GroupNotificationType {
    PENDING(0, "待处理"),
    REJECTED(2, "已拒绝"),
    EXPIRED(3, "已过期"),
    ADMIN_PENDING(4, "管理员待处理"),
    ADMIN_ACCEPTED(5, "管理员已同意"),
    ADMIN_REJECTED(6, "管理员已拒绝"),
    QUITED(7, "已退出"),
    KICKED(8, "已踢出"),
    SET_ADMIN(9, "设置为管理员"),
    CANCEL_ADMIN(10, "取消管理员"),
    SET_MUTE(11, "设置禁言"),
    CANCEL_MUTE(12, "取消禁言"),
    DISBAND(13, "解散");

    @EnumValue
    private final int value;
    private final String desc;

    GroupNotificationType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
