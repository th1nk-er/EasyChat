package top.th1nk.easychat.service.impl;

import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.GroupProperties;
import top.th1nk.easychat.config.easychat.MinioProperties;
import top.th1nk.easychat.config.easychat.UserProperties;
import top.th1nk.easychat.service.MinioService;

import java.io.ByteArrayInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
public class MinioServiceImpl implements MinioService {
    @Resource
    private MinioProperties minioProperties;
    @Resource
    private ResourceLoader resourceLoader;
    @Resource
    private UserProperties userProperties;
    @Resource
    private GroupProperties groupProperties;

    @Override
    public boolean upload(byte[] fileBytes, String filePath) {
        log.debug("[minio]上传文件 {}", filePath);
        try (MinioClient minioClient = getMinioClient()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(filePath)
                            .stream(new ByteArrayInputStream(fileBytes), fileBytes.length, -1)
                            .build());
            log.info("文件上传成功 {}", filePath);
            return true;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            return false;
        }
    }

    @Override
    public GetObjectResponse getObject(String filePath) {
        log.debug("[minio]获取文件 {}", filePath);
        try (MinioClient minioClient = getMinioClient()) {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(filePath)
                            .build());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean initBucket() {
        log.debug("[minio]初始化bucket");
        try (MinioClient minioClient = getMinioClient()) {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build());
            if (!found)
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            byte[] userAvatar = Files.readAllBytes(Paths.get(resourceLoader.getResource(userProperties.getDefaultAvatarPath()).getURI()));
            byte[] groupAvatar = Files.readAllBytes(Paths.get(resourceLoader.getResource(groupProperties.getDefaultAvatarPath()).getURI()));
            return upload(userAvatar, userProperties.getAvatarDir() + "/" + userProperties.getDefaultAvatarName())
                    && upload(groupAvatar, groupProperties.getAvatarDir() + "/" + groupProperties.getDefaultAvatarName());
        } catch (Exception e) {
            log.error("初始化Minio失败", e);
            return false;
        }
    }

    @Override
    public boolean deleteObject(String filePath) {
        log.debug("[minio]删除文件 {}", filePath);
        try (MinioClient minioClient = getMinioClient()) {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(minioProperties.getBucketName())
                    .object(filePath).build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpoint())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }
}
