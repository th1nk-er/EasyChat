package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;

@Getter
public class GroupException extends CustomException {
    public GroupException(GroupExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
