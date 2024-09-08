package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.dto.*;
import top.th1nk.easychat.domain.vo.SearchUserVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.exception.LoginException;
import top.th1nk.easychat.exception.RegisterException;

/**
 * @author th1nk
 * @description 针对表【ec_user】的数据库操作Service
 * @createDate 2024-07-08 14:05:15
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 通过用户名获取用户信息
     *
     * @param username 用户名
     * @return 用户信息Vo
     */
    UserVo getByUsername(String username);

    /**
     * 通过邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息Vo
     */
    @Nullable
    UserVo getByEmail(String email);

    /**
     * 用户注册
     *
     * @param registerDto 用户注册信息
     * @return 用户信息Vo
     * @throws RegisterException 注册异常
     */
    @Nullable
    UserVo register(RegisterDto registerDto) throws RegisterException;


    /**
     * 用户登录
     *
     * @param loginDto 用户登录信息
     * @return 用户Token
     * @throws LoginException 登录异常
     */
    UserTokenDto login(LoginDto loginDto) throws LoginException;

    /**
     * 判断用户名是否已注册
     *
     * @param username 用户名
     * @return 是否已注册
     */
    boolean isUsernameExist(String username);

    /**
     * 判断邮箱是否已注册
     *
     * @param email 邮箱
     * @return 是否已注册
     */
    boolean isEmailExist(String email);

    /**
     * 通过关键词搜索用户，模糊匹配
     *
     * @param keyword 关键词
     * @param page    页码
     * @return 搜索结果
     */
    SearchUserVo searchUser(String keyword, int page);

    /**
     * 修改密码
     *
     * @param updatePasswordDto 修改密码信息
     * @return 修改结果
     */
    boolean updatePassword(UpdatePasswordDto updatePasswordDto);

    /**
     * 修改头像
     *
     * @param userId 用户id
     * @param file   头像文件
     * @return 上传后的头像路径, null 表示上传失败
     */
    @Nullable
    String updateAvatar(int userId, MultipartFile file);

    /**
     * 修改用户信息
     *
     * @param updateUserInfoDto 用户信息
     * @return 修改结果
     */
    boolean updateUserInfo(UpdateUserInfoDto updateUserInfoDto);
}
