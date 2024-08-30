package top.th1nk.easychat.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final int code;
    private final String message;

    public CustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }
}