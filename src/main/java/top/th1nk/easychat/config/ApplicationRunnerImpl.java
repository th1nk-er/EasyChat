package top.th1nk.easychat.config;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.service.MinioService;

@Slf4j
@Component
public class ApplicationRunnerImpl implements ApplicationRunner {
    @Resource
    private MinioService minioService;

    @Override
    public void run(ApplicationArguments args) {
        if (minioService.initBucket()) {
            log.info("Minio存储桶初始化成功");
        } else {
            throw new RuntimeException("Minio存储桶初始化失败");
        }
    }
}
