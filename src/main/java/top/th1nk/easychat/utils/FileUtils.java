package top.th1nk.easychat.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.security.MessageDigest;

public class FileUtils {
    @NotNull
    public static String getFileType(@Nullable String fileName) {
        if (fileName == null) return "";
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 判断文件是否为图片
     *
     * @param fileName 文件名
     * @return 是否为图片
     */
    public static boolean isImage(String fileName) {
        if (fileName == null) return false;
        String[] imageSuffix = new String[]{"jpg", "jpeg", "png", "gif", "bmp", "webp"};
        for (String suffix : imageSuffix) {
            if (fileName.toLowerCase().endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 计算文件的校验和
     *
     * @param bytes 文件字节
     * @return 校验和，错误时返回空字符串
     */
    @NotNull
    public static String getCheckSum(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return StringUtils.bytesToHex(digest.digest(bytes));
        } catch (Exception e) {
            return "";
        }
    }
}
