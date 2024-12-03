package top.th1nk.easychat.exception.enums;

import lombok.Getter;

@Getter
public enum GroupExceptionEnum {
    INVALID_GROUP_NAME(5001, "群聊名称不合法"),
    CREATABLE_GROUP_LIMIT_EXCEEDED(5002, "你所能够创建的群聊已达到上限，无法创建新的群聊"),
    ALREADY_IN_GROUP(5003, "你已经在该群聊中"),
    NOT_ADMIN(5004, "你不是管理员"),
    GROUP_MEMBER_CREATE_FAIL(5005, "创建群组成员失败"),
    INVITATION_NOT_FOUND(5006, "邀请不存在"),
    LEADER_CANNOT_QUIT(5007, "群主不能退出群聊"),
    LEADER_CANNOT_BE_KICKED(5008, "群主不能被踢出群聊"),
    INSERT_GROUP_MEMBER_FAIL(5009, "添加群组成员失败"),
    INVALID_GROUP_DESCRIPTION(5010, "群聊简介不合法"),
    GROUP_DISBAND(5011, "群聊已解散，无法操作"),
    ;
    private final int code;
    private final String message;

    GroupExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
