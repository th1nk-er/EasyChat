package top.th1nk.easychat;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.utils.StringUtils;
import top.th1nk.easychat.utils.UserUtils;

@SpringBootTest
class EasyChatApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(EasyChatApplicationTests.class);
    @Resource
    EmailService emailService;

    @Test
    void TestMail() {
        emailService.sendVerifyCodeEmail("seven@th1nk.top", StringUtils.getRandomString(6, 2));
    }

    @Test
    void TestPasswordEncoder() {
        log.info(UserUtils.encryptPassword("123456"));
    }
}
