package top.th1nk.easychat;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.utils.RandomUtils;

@SpringBootTest
class EasyChatApplicationTests {
    @Resource
    EmailService emailService;

    @Test
    void TestMail() {
        emailService.sendVerifyCodeEmail("seven@th1nk.top", RandomUtils.getRandomString(6, 2));
    }
}
