package top.th1nk.easychat.utils;

import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SecurityUtils {

    @Resource
    private SysUserFriendMapper sysUserFriendMapper;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;

    @Cacheable(cacheNames = "user:perms", key = "#userId")
    public Set<String> getPermissions(Integer userId) {
        Set<String> permissions = new HashSet<>();
        permissions.add("USER:" + userId);
        List<SysUserFriend> sysUserFriends = sysUserFriendMapper.selectAllByUid(userId);
        sysUserFriends.forEach(sysUserFriend -> permissions.add("FRIEND:" + sysUserFriend.getFriendId()));
        List<SysGroupMember> sysGroupMembers = sysGroupMemberMapper.selectAllByUserId(userId);
        sysGroupMembers.forEach(sysGroupMember -> {
            permissions.add("GROUP:" + sysGroupMember.getGroupId());
            if (sysGroupMember.getRole() == UserRole.ADMIN)
                permissions.add("GROUP_ADMIN:" + sysGroupMember.getGroupId());
            else if (sysGroupMember.getRole() == UserRole.LEADER) {
                permissions.add("GROUP_LEADER:" + sysGroupMember.getGroupId());
                permissions.add("GROUP_ADMIN:" + sysGroupMember.getGroupId());
            }
        });
        return permissions;
    }
}
