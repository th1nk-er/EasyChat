package top.th1nk.easychat.controller;

import io.minio.GetObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.config.easychat.ChatProperties;
import top.th1nk.easychat.config.easychat.UserProperties;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.service.MinioService;
import top.th1nk.easychat.utils.FileUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传", description = "文件上传API")
public class FileController {
    @Resource
    private MinioService minioService;
    @Resource
    private UserProperties userProperties;
    @Resource
    private ChatProperties chatProperties;

    @Operation(summary = "获取头像", description = "以文件形式返回用户头像")
    @GetMapping("/avatar/{imgName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String imgName) {
        GetObjectResponse objectResponse = minioService.getObject(userProperties.getAvatarDir() + "/" + imgName);
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

    @Operation(summary = "上传聊天图片", description = "上传聊天图片")
    @PostMapping("/chat/image")
    public Response<String> uploadImg(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > chatProperties.getImageMaxSize())
                throw new CommonException(CommonExceptionEnum.FILE_SIZE_EXCEEDED);
            if (!FileUtils.isImage(file.getOriginalFilename()))
                throw new CommonException(CommonExceptionEnum.FILE_TYPE_NOT_SUPPORTED);
            byte[] bytes = file.getInputStream().readAllBytes();
            String checkSum = FileUtils.getCheckSum(bytes);
            String fileType = FileUtils.getFileType(file.getOriginalFilename());
            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String filePath = "/" + chatProperties.getFileDir() + "/" + date + "/" + checkSum + "." + fileType;
            if (minioService.getObject(filePath) == null) {
                minioService.upload(bytes, filePath);
            }
            return Response.ok(filePath);
        } catch (IOException e) {
            throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
        }
    }

    @Operation(summary = "获取图片", description = "以文件形式返回聊天图片")
    @GetMapping("/chat-file/{imgDate}/{imgName}")
    public ResponseEntity<byte[]> getChatImg(@PathVariable String imgDate, @PathVariable String imgName) {
        GetObjectResponse objectResponse = minioService.getObject(chatProperties.getFileDir() + "/" + imgDate + "/" + imgName);
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
            log.error("获取聊天图片失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
