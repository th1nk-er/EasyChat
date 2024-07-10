package top.th1nk.easychat.service.impl;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.utils.UserUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    private static final String APPLICATION_NAME = "${APPLICATION_NAME}";
    private static final String VERIFY_CODE = "${VERIFY_CODE}";
    private static final String EXPIRE_TIME = "${EXPIRE_TIME}";
    private static final String CODE_PREFIX = "email:code:";
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private ResourceLoader resourceLoader;
    @Value("${easy-chat.mail.verify-code.template.path}")
    private String templatePath;
    @Value("${spring.application.name}")
    private String applicationName;
    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${easy-chat.mail.sender.name}")
    private String senderName;
    @Value("${easy-chat.mail.verify-code.expire:15}")
    private int expireTime;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void sendVerifyCodeEmail(String sendTo, String verifyCode) throws CommonException {
        if (!UserUtils.isValidEmail(sendTo)) throw new CommonException(CommonExceptionEnum.EMAIL_INVALID);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(senderEmail, senderName);
            helper.setTo(sendTo);
            helper.setSubject(applicationName + "验证码:" + verifyCode);
            byte[] fileData = Files.readAllBytes(Paths.get(resourceLoader.getResource(templatePath).getURI()));
            String content = new String(fileData, StandardCharsets.UTF_8);
            content = content.replace(APPLICATION_NAME, applicationName);
            content = content.replace(VERIFY_CODE, verifyCode);
            content = content.replace(EXPIRE_TIME, String.valueOf(expireTime));
            helper.setText(content, true);
            log.info("发送验证码邮件:{},{}", sendTo, verifyCode);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.error("创建multipart失败");
            throw new CommonException(CommonExceptionEnum.EMAIL_SEND_FAILED);
        } catch (MailException e) {
            log.error("邮件发送失败");
            throw new CommonException(CommonExceptionEnum.EMAIL_SEND_FAILED);
        } catch (IOException e) {
            log.error("模板文件路径解析失败");
            throw new CommonException(CommonExceptionEnum.EMAIL_SEND_FAILED);
        }
    }

    @Override
    public void saveVerifyCode(String email, String verifyCode) {
        log.info("存储验证码:{},{}", email, verifyCode);
        stringRedisTemplate.opsForValue().set(CODE_PREFIX + email, verifyCode, Duration.ofMinutes(expireTime));
    }

    @Override
    public boolean verifyCode(String email, String verifyCode) {
        log.info("验证验证码:{},{}", email, verifyCode);
        String code = stringRedisTemplate.opsForValue().get(CODE_PREFIX + email);
        if (code == null) throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_EXPIRE);
        if (code.equals(verifyCode)) {
            // 验证成功，删除验证码
            stringRedisTemplate.delete(CODE_PREFIX + email);
            return true;
        }
        return false;
    }
}
