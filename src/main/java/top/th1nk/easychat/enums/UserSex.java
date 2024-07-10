package top.th1nk.easychat.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum UserSex {
    MALE(0, "男"),
    FEMALE(1, "女"),
    SECRET(2, "保密");

    @EnumValue
    private final int value;
    private final String desc;

    UserSex(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

}
