package top.th1nk.easychat.controller;

import io.minio.GetObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.th1nk.easychat.service.MinioService;

@Slf4j
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传API")
public class FileController {
    @Resource
    private MinioService minioService;

    @Operation(summary = "获取头像", description = "以文件形式返回用户头像")
    @GetMapping("/avatar/{imgName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String imgName) {
        GetObjectResponse objectResponse = minioService.getObject("avatar/" + imgName);
        if (objectResponse == null) return ResponseEntity.notFound().build();
        String contentType = objectResponse.headers().get("Content-Type");
        if (contentType == null) contentType = "image/jpeg";
        try {
            byte[] avatarBytes = objectResponse.readAllBytes();
            objectResponse.close();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imgName + "\"")
                    .contentType(MediaType.valueOf(contentType))
                    .body(avatarBytes);
        } catch (Exception e) {
            log.error("获取头像失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
