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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.MailProperties;
import top.th1nk.easychat.enums.CommonExceptionEnum;
import top.th1nk.easychat.enums.EmailActionEnum;
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
@EnableAsync
public class EmailServiceImpl implements EmailService {
    private static final String CODE_PREFIX = "email:code:";
    @Resource
    private MailProperties mailProperties;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource
    private ResourceLoader resourceLoader;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;
    @Value("${spring.application.name}")
    private String applicationName;


    @Override
    public boolean isEmailSendFrequently(String email, EmailActionEnum emailAction) {
        if (!UserUtils.isValidEmail(email)) throw new CommonException(CommonExceptionEnum.EMAIL_INVALID);
        Long expire = stringRedisTemplate.getExpire(CODE_PREFIX + emailAction.getCode() + ":" + email);
        if (expire == null || expire <= 0) return false;
        // 1分钟之内只能发送一次
        return mailProperties.getVerifyCode().getExpire() * 60L - expire < 60L;
    }

    @Async
    @Override
    public void sendVerifyCodeEmail(String sendTo, String verifyCode, EmailActionEnum emailAction) throws CommonException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(senderEmail, mailProperties.getSender().getName());
            helper.setTo(sendTo);
            helper.setSubject(applicationName + "验证码:" + verifyCode);
            byte[] fileData = Files.readAllBytes(Paths.get(resourceLoader.getResource(mailProperties.getVerifyCode().getTemplate().getPath()).getURI()));
            String content = new String(fileData, StandardCharsets.UTF_8);
            content = content.replace(mailProperties.getVerifyCode().getTemplate().getApplicationNamePlaceholder(), applicationName);
            content = content.replace(mailProperties.getVerifyCode().getTemplate().getCodePlaceholder(), verifyCode);
            content = content.replace(mailProperties.getVerifyCode().getTemplate().getExpirePlaceholder(), String.valueOf(mailProperties.getVerifyCode().getExpire()));
            content = content.replace(mailProperties.getVerifyCode().getTemplate().getActionPlaceholder(), emailAction.getDesc());
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
    public void saveVerifyCode(String email, String verifyCode, EmailActionEnum emailAction) {
        log.info("存储验证码:{},{}", email, verifyCode);
        stringRedisTemplate.opsForValue().set(CODE_PREFIX + emailAction.getCode() + ":" + email, verifyCode, Duration.ofMinutes(mailProperties.getVerifyCode().getExpire()));
    }

    @Override
    public boolean verifyCode(String email, String verifyCode, EmailActionEnum emailAction) {
        log.info("验证验证码:{},{}", email, verifyCode);
        String code = stringRedisTemplate.opsForValue().get(CODE_PREFIX + emailAction.getCode() + ":" + email);
        if (code == null) throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_EXPIRE);
        if (code.equals(verifyCode)) {
            // 验证成功，删除验证码
            stringRedisTemplate.delete(CODE_PREFIX + email);
            return true;
        }
        return false;
    }
}
