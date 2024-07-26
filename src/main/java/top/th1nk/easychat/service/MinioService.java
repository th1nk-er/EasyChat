package top.th1nk.easychat.service;

import io.minio.GetObjectResponse;
import jakarta.annotation.Nullable;

/**
 * minio服务
 */
public interface MinioService {
    /**
     * 指定文件路径上传文件
     *
     * @param fileBytes 文件字节数组
     * @param filePath  上传后的路径
     * @return 是否上传成功
     */
    boolean upload(byte[] fileBytes, String filePath);

    /**
     * 获取文件
     *
     * @param filePath 文件路径
     * @return 返回数据
     */
    @Nullable
    GetObjectResponse getObject(String filePath);

    /**
     * 初始化bucket
     *
     * @return 是否初始化成功
     */
    boolean initBucket();
}
