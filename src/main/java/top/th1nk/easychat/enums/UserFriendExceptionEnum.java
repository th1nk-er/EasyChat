package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum UserFriendExceptionEnum {
    ALREADY_FRIEND(4001, "已经是好友"),
    ADD_FRIEND_FAILED(4002, "添加好友失败"),
    ADD_REQUEST_EXIST(4003, "已有好友申请正在等待处理"),
    ADD_REQUEST_EXPIRED(4004, "好友申请已过期"),
    CANNOT_ADD_SELF(4005, "不能添加自己为好友"),
    NOT_FRIEND(4006, "不是好友关系"),
    UPDATE_FRIEND_FAILED(4007, "更新好友信息失败"),
    INVALID_REMARK(4008, "好友备注不合法"),
    DELETE_FRIEND_FAILED(4009, "删除好友失败"),
    ;
    private final int code;
    private final String message;

    UserFriendExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
