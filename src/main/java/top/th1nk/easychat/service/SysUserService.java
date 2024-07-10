package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.vo.UserVo;

/**
 * @author th1nk
 * @description 针对表【ec_user】的数据库操作Service
 * @createDate 2024-07-08 14:05:15
 */
public interface SysUserService extends IService<SysUser> {
    /**
     * 根据用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户信息实体类
     */
    SysUser getUserByUsername(String username);

    /**
     * 根据邮箱查询用户信息
     *
     * @param email 邮箱
     * @return 用户信息实体类
     */
    SysUser getUserByEmail(String email);

    /**
     * 用户注册
     *
     * @param registerDto 用户注册信息
     * @return 用户信息Vo
     */
    UserVo register(RegisterDto registerDto);
}
