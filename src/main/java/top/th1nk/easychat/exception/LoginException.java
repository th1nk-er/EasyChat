package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.enums.LoginExceptionEnum;

/**
 * 登录异常
 */
@Getter
public class LoginException extends RuntimeException {

    private final String message;
    private final int code;

    public LoginException(LoginExceptionEnum e) {
        this.message = e.getMessage();
        this.code = e.getCode();
    }
}
