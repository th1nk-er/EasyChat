package top.th1nk.easychat.utils;

import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.vo.UserVo;

/**
 * 用户工具类
 */
public class UserUtils {
    /**
     * 将用户信息转换为VO
     *
     * @param user 用户信息
     * @return 用户信息VO
     */
    public static UserVo userToVo(SysUser user) {
        if (user == null) return null;
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        return userVo;
    }

    /**
     * 验证用户名是否合法
     *
     * @param username 用户名
     * @return 合法-true 不合法-false
     */
    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    /**
     * 验证密码是否合法
     *
     * @param password 密码
     * @return 合法-true 不合法-false
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6 && password.length() <= 20;
    }

    /**
     * 验证邮箱是否合法
     *
     * @param email 邮箱
     * @return 合法-true 不合法-false
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    /**
     * 加密密码
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
