package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.dto.LoginDto;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.dto.UserTokenDto;
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
     * 用户注册
     *
     * @param registerDto 用户注册信息
     * @return 用户信息Vo
     * @throws RegisterException 注册异常
     */
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
}
