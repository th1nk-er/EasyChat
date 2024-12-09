package top.th1nk.easychat.utils;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.io.FileInputStream;
import java.security.DigestInputStream;
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

    /**
     * 计算文件的校验和
     *
     * @param filePath 文件路径
     * @return 校验和，错误时返回空字符串
     */
    public static String getCheckSum(String filePath) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(filePath);
            DigestInputStream dis = new DigestInputStream(fis, md);
            // 读取文件内容，计算哈希值
            byte[] buffer = new byte[8192];
            int read = dis.read(buffer);
            while (read > 0) {
                read = dis.read(buffer);
            }
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
