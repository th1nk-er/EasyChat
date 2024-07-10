package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.enums.RegisterExceptionEnum;

@Getter
public class RegisterException extends RuntimeException {
    private final String message;
    private final int code;

    public RegisterException(RegisterExceptionEnum e) {
        this.message = e.getMessage();
        this.code = e.getCode();
    }
}
