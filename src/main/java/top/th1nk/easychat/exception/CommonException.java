package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.enums.CommonExceptionEnum;

@Getter
public class CommonException extends RuntimeException {
    private final String message;
    private final int code;

    public CommonException(CommonExceptionEnum e) {
        this.message = e.getMessage();
        this.code = e.getCode();
    }
}
