package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum LoginExceptionEnum {

    ;

    private final int code;
    private final String message;

    LoginExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
