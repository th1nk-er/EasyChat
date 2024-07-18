package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum AddUserType {
    ADD_OTHER(0, "添加对方"),
    ADD_BY_OTHER(1, "被对方添加");;
    @EnumValue
    private final int code;
    private final String desc;

    AddUserType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
