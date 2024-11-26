package top.th1nk.easychat.exception;

import lombok.Getter;
import top.th1nk.easychat.exception.enums.DataExceptionEnum;

@Getter
public class DataException extends CustomException {
    public DataException(DataExceptionEnum e) {
        super(e.getCode(), e.getMessage());
    }
}
