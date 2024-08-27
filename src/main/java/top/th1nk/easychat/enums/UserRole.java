package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserRole {
    USER(0, "普通用户"),
    ADMIN(1, "管理员"),
    LEADER(2, "群主"),
    ;

    @EnumValue
    private final int value;
    private final String desc;

    UserRole(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
