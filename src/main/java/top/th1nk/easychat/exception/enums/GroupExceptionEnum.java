package top.th1nk.easychat.exception.enums;

import lombok.Getter;

@Getter
public enum GroupExceptionEnum {
    INVALID_GROUP_NAME(5001, "群聊名称不合法"),
    CREATABLE_GROUP_LIMIT_EXCEEDED(5002, "你所能够创建的群聊已达到上限，无法创建新的群聊"),
    GROUP_CREATE_FAIL(5003, "创建群聊失败"),
    ALREADY_IN_GROUP(5004, "你已经在该群聊中"),
    NOT_ADMIN(5005, "你不是管理员"),
    GROUP_MEMBER_CREATE_FAIL(5006, "创建群组成员失败"),
    ;
    private final int code;
    private final String message;

    GroupExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
