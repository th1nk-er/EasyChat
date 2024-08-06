package top.th1nk.easychat.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.service.MinioService;
import top.th1nk.easychat.service.SysUserConversationService;

@Slf4j
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Resource
    private MinioService minioService;
    @Resource
    private SysUserConversationService sysUserConversationService;

    @Override
    public void run(ApplicationArguments args) {
        if (minioService.initBucket()) {
            log.info("Minio存储桶初始化成功");
        } else {
            log.error("Minio存储桶初始化失败，请检查服务状态以及配置参数");
        }
        sysUserConversationService.loadConversationToRedis();
    }
}
