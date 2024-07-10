package top.th1nk.easychat.utils;

import java.util.Random;

public class RandomUtils {
    private static final String CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 生成随机字符串
     *
     * @param length 字符串长度
     * @param mode   模式 0-包含字母和数字 1-仅包含字母 2-仅包含数字
     * @return 随机字符串
     */
    public static String getRandomString(int length, int mode) {
        StringBuilder sb = new StringBuilder();
        switch (mode) {
            case 0:
                for (int i = 0; i < length; i++) {
                    int index = new Random().nextInt(CHARACTERS.length());
                    sb.append(CHARACTERS.charAt(index));
                }
                break;
            case 1:
                for (int i = 0; i < length; i++) {
                    int index = new Random().nextInt(CHARACTERS.length() - 10);
                    char c = CHARACTERS.charAt(index + 10);
                    sb.append(c);
                }
                break;
            case 2:
                for (int i = 0; i < length; i++) {
                    int index = new Random().nextInt(10);
                    char c = CHARACTERS.charAt(index);
                    sb.append(c);
                }
        }
        return sb.toString();
    }

    /**
     * 生成随机字符串 包含大小写字母、数字
     *
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String getRandomString(int length) {
        return getRandomString(length, 0);
    }

    /**
     * 生成随机数字
     *
     * @param length 字符串长度
     * @return 随机数字
     */
    public static String getRandomNumber(int length) {
        return getRandomString(length, 2);
    }
}
