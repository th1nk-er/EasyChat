package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.utils.SecurityUtils;

/**
 * 自定义用户信息
 */
@Slf4j
@Service
public class PasswordUserDetailsService implements UserDetailsService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SecurityUtils securityUtils;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.getByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(LoginExceptionEnum.USERNAME_OR_PASSWORD_ERROR.getMessage());
        user.setPermissions(securityUtils.getPermissions(user.getId()));
        return user;
    }
}
