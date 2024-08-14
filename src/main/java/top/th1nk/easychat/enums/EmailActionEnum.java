package top.th1nk.easychat.enums;

import lombok.Getter;

/**
 * 用于表示发送邮件验证码的类型
 */
@Getter
public enum EmailActionEnum {
    ACTION_REGISTER("REGISTER", "注册账号"),
    ACTION_LOGIN("LOGIN", "登录操作"),
    ACTION_CHANGE_PASSWORD("CHANGE_PASSWORD", "修改密码");

    private final String code;
    private final String desc;

    EmailActionEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}