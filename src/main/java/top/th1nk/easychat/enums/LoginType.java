package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum LoginType {
    PASSWORD(0, "密码登录"),
    EMAIL(1, "邮箱登录");
    private final int code;
    private final String desc;

    LoginType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
