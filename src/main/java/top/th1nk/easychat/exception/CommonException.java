package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;

@Getter
public class CommonException extends CustomException {
    public CommonException(CommonExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
