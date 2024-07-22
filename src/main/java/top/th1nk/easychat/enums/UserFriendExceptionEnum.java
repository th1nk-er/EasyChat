package top.th1nk.easychat.enums;

import lombok.Getter;

@Getter
public enum UserFriendExceptionEnum {
    ALREADY_FRIEND(4001, "已经是好友"),
    ADD_FRIEND_FAILED(4002, "添加好友失败"),
    ADD_REQUEST_EXIST(4003, "已有好友申请正在等待处理"),
    ;
    private final int code;
    private final String message;

    UserFriendExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
