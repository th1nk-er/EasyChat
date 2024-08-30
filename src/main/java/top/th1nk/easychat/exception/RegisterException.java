package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.RegisterExceptionEnum;

@Getter
public class RegisterException extends CustomException {
    public RegisterException(RegisterExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
