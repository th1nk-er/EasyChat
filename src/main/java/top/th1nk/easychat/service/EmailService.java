package top.th1nk.easychat.service;

import top.th1nk.easychat.exception.CommonException;

/**
 * 邮件服务
 */
public interface EmailService {
    /**
     * 发送验证码邮件
     *
     * @param sendTo     收件人
     * @param verifyCode 验证码
     * @throws CommonException 邮件发送失败时抛出异常
     */
    void sendVerifyCodeEmail(String sendTo, String verifyCode) throws CommonException;

    /**
     * 保存验证码
     *
     * @param email      邮箱
     * @param verifyCode 验证码
     */
    void saveVerifyCode(String email, String verifyCode);

    /**
     * 验证码验证
     *
     * @param email      邮箱
     * @param verifyCode 验证码
     * @return 验证码是否正确
     * @throws CommonException 验证码过期时抛出异常
     */
    boolean verifyCode(String email, String verifyCode) throws CommonException;


    /**
     * 在验证码发送之前进行的操作
     *
     * @param email 邮箱
     */
    void beforeSendVerifyCode(String email);
}
