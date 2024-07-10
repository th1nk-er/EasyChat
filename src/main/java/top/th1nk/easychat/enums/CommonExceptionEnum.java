package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum CommonExceptionEnum {
    USERNAME_INVALID(1001, "用户名不合法"),
    PASSWORD_INVALID(1002, "密码不合法"),
    EMAIL_INVALID(1003, "邮箱不合法"),
    EMAIL_SEND_FAILED(1004, "邮件发送失败"),
    PARAM_INVALID(1005, "参数错误"),
    EMAIL_VERIFY_CODE_EXPIRE(1006, "邮箱验证码已过期"),
    EMAIL_VERIFY_CODE_ERROR(1007, "邮箱验证码错误"),
    ;
    private final String message;
    private final int code;

    CommonExceptionEnum(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
