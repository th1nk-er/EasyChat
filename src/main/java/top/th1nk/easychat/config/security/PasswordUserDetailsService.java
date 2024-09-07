package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义用户信息
 */
@Slf4j
@Service
public class PasswordUserDetailsService implements UserDetailsService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserFriendMapper sysUserFriendMapper;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.getByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException(LoginExceptionEnum.USERNAME_OR_PASSWORD_ERROR.getMessage());
        user.setPermissions(getPermissions(user.getId()));
        return user;
    }

    private Set<String> getPermissions(Integer userId) {
        Set<String> permissions = new HashSet<>();
        permissions.add("USER:" + userId);
        List<SysUserFriend> sysUserFriends = sysUserFriendMapper.selectAllByUid(userId);
        sysUserFriends.forEach(sysUserFriend -> permissions.add("FRIEND:" + sysUserFriend.getFriendId()));
        List<SysGroupMember> sysGroupMembers = sysGroupMemberMapper.selectAllByUserId(userId);
        sysGroupMembers.forEach(sysGroupMember -> {
            permissions.add("GROUP:" + sysGroupMember.getGroupId());
            if (sysGroupMember.getRole() == UserRole.ADMIN)
                permissions.add("GROUP_ADMIN:" + sysGroupMember.getGroupId());
            else if (sysGroupMember.getRole() == UserRole.LEADER)
                permissions.add("GROUP_LEADER:" + sysGroupMember.getGroupId());
        });
        return permissions;
    }
}
