package top.th1nk.easychat.exception.enums;

import lombok.Getter;

@Getter
public enum LoginExceptionEnum {
    EMAIL_VERIFY_CODE_ERROR(3001, "邮箱验证码错误"),
    EMAIL_NOT_REGISTER(3002, "邮箱未注册"),
    USERNAME_OR_PASSWORD_ERROR(3003, "用户名或密码错误"),
    ;

    private final int code;
    private final String message;

    LoginExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
