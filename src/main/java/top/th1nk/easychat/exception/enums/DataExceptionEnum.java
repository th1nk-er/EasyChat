package top.th1nk.easychat.exception.enums;

import lombok.Getter;

@Getter
public enum DataExceptionEnum {
    DATA_INSERT_FAILED(6001, "数据插入失败"),
    DATA_UPDATE_FAILED(6002, "数据更新失败"),
    ;
    private final String message;
    private final int code;

    DataExceptionEnum(int code, String message) {
        this.message = message;
        this.code = code;
    }
}
