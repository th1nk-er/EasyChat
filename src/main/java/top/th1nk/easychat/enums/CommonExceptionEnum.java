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
    TOKEN_INVALID(1007, "令牌无效"),
    TOKEN_EXPIRE(1008, "令牌过期"),
    RESOURCE_NOT_FOUND(1009, "资源不存在"),
    METHOD_NOT_ALLOWED(1010, "请求方法不允许"),
    EMAIL_VERIFY_CODE_SEND__FREQUENTLY(1011, "验证码发送太频繁"),
    USER_NOT_FOUND(1012, "用户不存在"),
    ;
    private final String message;
    private final int code;

    CommonExceptionEnum(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
