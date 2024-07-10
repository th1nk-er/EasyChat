package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.enums.CommonExceptionEnum;

@Getter
public class CommonException extends RuntimeException {
    private final String message;

    public CommonException(CommonExceptionEnum e) {
        this.message = e.getMessage();
    }
}
