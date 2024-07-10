package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum RegisterExceptionEnum {
    USERNAME_EXIST(2001, "用户名已被注册"),

    EMAIL_EXIST(2002, "邮箱已被注册"),
    REGISTER_FAILED(2003, "注册失败"),
    ;

    private final int code;
    private final String message;

    RegisterExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
