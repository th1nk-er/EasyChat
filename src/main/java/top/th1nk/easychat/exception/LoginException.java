package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;

/**
 * 登录异常
 */
@Getter
public class LoginException extends CustomException {
    public LoginException(LoginExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
