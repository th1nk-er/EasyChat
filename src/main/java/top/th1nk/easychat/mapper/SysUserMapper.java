package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUser;

/**
 * @author th1nk
 * @description 针对表【ec_user】的数据库操作Mapper
 * @createDate 2024-07-08 14:05:15
 * @Entity top.th1nk.easychat.domain.SysUser
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    SysUser getByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户
     */
    SysUser getByEmail(String email);

    /**
     * 更新登录ip
     *
     * @param username 用户名
     * @param ip       ip
     */
    void updateLoginIp(String username, String ip);
}




