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
     * 上传文件
     *
     * @param localPath 本地文件路径
     * @param filePath  上传后的路径
     * @return 是否上传成功
     */
    boolean upload(String localPath, String filePath);

    /**
     * 获取文件
     *
     * @param filePath 文件路径
     * @return 返回数据
     */
    @Nullable
    GetObjectResponse getObject(String filePath);

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean isObjectExist(String filePath);

    /**
     * 初始化bucket
     *
     * @return 是否初始化成功
     */
    boolean initBucket();

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteObject(String filePath);
}
