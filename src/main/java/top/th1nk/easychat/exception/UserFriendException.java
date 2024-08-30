package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.UserFriendExceptionEnum;

@Getter
public class UserFriendException extends CustomException {
    public UserFriendException(UserFriendExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
