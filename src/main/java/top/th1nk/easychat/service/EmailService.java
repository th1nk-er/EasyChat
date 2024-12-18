package top.th1nk.easychat.service;

import top.th1nk.easychat.enums.EmailActionEnum;
import top.th1nk.easychat.exception.CommonException;

/**
 * 邮件服务
 */
public interface EmailService {
    /**
     * 判断是否发送验证码太频繁
     *
     * @param email       邮箱
     * @param emailAction 验证码类型
     * @return 是否频繁
     */
    boolean isEmailSendFrequently(String email, EmailActionEnum emailAction);

    /**
     * 发送验证码邮件
     *
     * @param sendTo      收件人
     * @param verifyCode  验证码
     * @param emailAction 验证码类型
     * @throws CommonException 邮件发送失败时抛出异常
     */
    void sendVerifyCodeEmail(String sendTo, String verifyCode, EmailActionEnum emailAction) throws CommonException;

    /**
     * 保存验证码
     *
     * @param email       邮箱
     * @param verifyCode  验证码
     * @param emailAction 验证码类型
     */
    void saveVerifyCode(String email, String verifyCode, EmailActionEnum emailAction);

    /**
     * 验证码验证，验证成功后立刻删除验证码
     *
     * @param email       邮箱
     * @param verifyCode  验证码
     * @param emailAction 验证码类型
     * @return 验证码是否正确
     * @throws CommonException 验证码过期时抛出异常
     */
    boolean verifyCode(String email, String verifyCode, EmailActionEnum emailAction) throws CommonException;

    /**
     * 验证码验证
     *
     * @param email       邮箱
     * @param verifyCode  验证码
     * @param emailAction 验证码类型
     * @param delete      验证成功后是否删除验证码
     * @return 验证码是否正确
     * @throws CommonException 验证码过期时抛出异常
     */
    boolean verifyCode(String email, String verifyCode, EmailActionEnum emailAction, boolean delete) throws CommonException;
}
