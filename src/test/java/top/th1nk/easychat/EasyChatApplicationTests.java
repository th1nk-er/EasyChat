package top.th1nk.easychat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import top.th1nk.easychat.utils.UserUtils;

@SpringBootTest
class EasyChatApplicationTests {
    private static final Logger log = LoggerFactory.getLogger(EasyChatApplicationTests.class);

    @Test
    void TestPasswordEncoder() {
        log.info(UserUtils.encryptPassword("123456"));
    }
}
