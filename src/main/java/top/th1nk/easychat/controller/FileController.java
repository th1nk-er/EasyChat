package top.th1nk.easychat.controller;

import io.minio.GetObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.config.easychat.ChatProperties;
import top.th1nk.easychat.config.easychat.GroupProperties;
import top.th1nk.easychat.config.easychat.UserProperties;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.service.MinioService;
import top.th1nk.easychat.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Resource
    private GroupProperties groupProperties;

    /**
     * 从minio中获取图片，并以ResponseEntity类型返回
     *
     * @param imgName 图片文件名
     * @param imgDir  图片文件夹
     * @return ResponseEntity
     */
    @NotNull
    private ResponseEntity<byte[]> getImageResponseEntity(String imgName, String imgDir) throws IOException {
        GetObjectResponse objectResponse = minioService.getObject(imgDir + "/" + imgName);
        if (objectResponse == null) return ResponseEntity.notFound().build();
        String contentType = objectResponse.headers().get("Content-Type");
        if (contentType == null) contentType = "image/jpeg";
        byte[] avatarBytes = objectResponse.readAllBytes();
        objectResponse.close();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imgName + "\"")
                .contentType(MediaType.valueOf(contentType))
                .body(avatarBytes);
    }

    private ResponseEntity<InputStreamSource> getFileResponseEntity(String fileName, String filePath, @Nullable String specificName) throws IOException {
        InputStream objectResponse = minioService.getObject(filePath + "/" + fileName);
        if (objectResponse == null) return ResponseEntity.notFound().build();
        String name = specificName == null ? fileName : specificName;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + name + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(objectResponse));
    }

    @Operation(summary = "获取头像", description = "以文件形式返回用户头像")
    @GetMapping("/avatar/{imgName}")
    public ResponseEntity<byte[]> getAvatar(@PathVariable String imgName) {
        try {
            return getImageResponseEntity(imgName, userProperties.getAvatarDir());
        } catch (IOException e) {
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

    @Operation(summary = "获取聊天文件", description = "返回聊天文件，图片/文件")
    @GetMapping("/chat-file/{fileDate}/{fileName}")
    public ResponseEntity<?> getChatFile(@PathVariable String fileDate, @PathVariable String fileName, @RequestParam("name") @Nullable String specificName) {
        try {
            if (FileUtils.isImage(fileName))
                return getImageResponseEntity(fileName, chatProperties.getFileDir() + "/" + fileDate);
            else {
                return getFileResponseEntity(fileName, chatProperties.getFileDir() + "/" + fileDate, specificName);
            }
        } catch (IOException e) {
            log.error("获取聊天文件失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "获取群头像", description = "以文件形式返回群头像")
    @GetMapping("/group/avatar/{imgName}")
    public ResponseEntity<byte[]> getGroupAvatar(@PathVariable String imgName) {
        try {
            return getImageResponseEntity(imgName, groupProperties.getAvatarDir());
        } catch (IOException e) {
            log.error("获取群头像失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "上传文件分片", description = "上传文件分片")
    @PostMapping("/chat/file/chunk")
    public Response<String> uploadFileChunk(@RequestParam("chunk") MultipartFile chunk,
                                            @RequestParam("fileName") String filename,
                                            @RequestParam("total") int total,
                                            @RequestParam("index") int index) {
        String tempDir = System.getProperty("java.io.tmpdir");
        try {
            if (chunk.getSize() > chatProperties.getFileMaxSize())
                throw new CommonException(CommonExceptionEnum.FILE_SIZE_EXCEEDED);
            String tempFilePath = Paths.get(tempDir, filename + "_" + index).toString();
            chunk.transferTo(new File(tempFilePath));
            for (int i = 0; i < total; i++) {
                if (!new File(Paths.get(tempDir, filename + "_" + i).toString()).exists()) {
                    return Response.ok();
                }
            }
            // 合并分片
            String localPath = Paths.get(tempDir, filename).toString();
            FileOutputStream fos = new FileOutputStream(localPath);
            for (int i = 0; i < total; i++) {
                Path chunkPath = Paths.get(tempDir, filename + "_" + i);
                Files.copy(chunkPath, fos);
                Files.delete(chunkPath); // 删除已合并的分片
            }
            fos.close();
            String checkSum = FileUtils.getCheckSum(localPath);
            String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String filePath = "/" + chatProperties.getFileDir() + "/" + date + "/" + checkSum + "." + FileUtils.getFileType(filename);
            if (minioService.isObjectExist(filePath)) {
                return Response.ok(filePath);
            }
            if (minioService.upload(localPath, filePath)) {
                return Response.ok(filePath);
            }
            throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
        } catch (IOException e) {
            throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
        } finally {
            try {
                String localPath = Paths.get(tempDir, filename).toString();
                Files.delete(Paths.get(localPath)); // 删除合并后的文件
            } catch (IOException _) {
            }
        }
    }
}
