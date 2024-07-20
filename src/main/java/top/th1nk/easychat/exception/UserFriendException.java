package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.enums.UserFriendExceptionEnum;

@Getter
public class UserFriendException extends RuntimeException {
    private final String message;
    private final int code;

    public UserFriendException(UserFriendExceptionEnum e) {
        this.message = e.getMessage();
        this.code = e.getCode();
    }
}
