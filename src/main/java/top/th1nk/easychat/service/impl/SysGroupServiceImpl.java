package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.th1nk.easychat.config.easychat.GroupProperties;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.SysGroupInvitation;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.domain.vo.UserGroupVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.GroupInvitationStatus;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupInvitationMapper;
import top.th1nk.easychat.mapper.SysGroupMapper;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.service.SysGroupService;
import top.th1nk.easychat.service.SysUserConversationService;
import top.th1nk.easychat.utils.GroupUtils;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group】的数据库操作Service实现
 * @createDate 2024-08-27 22:20:11
 */
@Slf4j
@Service
public class SysGroupServiceImpl extends ServiceImpl<SysGroupMapper, SysGroup>
        implements SysGroupService {
    @Resource
    private GroupProperties groupProperties;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SysUserFriendMapper sysUserFriendMapper;
    @Resource
    private SysGroupInvitationMapper sysGroupInvitationMapper;
    @Resource
    private SysUserConversationService sysUserConversationService;

    @Transactional
    @Override
    public boolean createGroup(CreateGroupDto createGroupDto) {
        if (!GroupUtils.isValidGroupName(createGroupDto.getGroupName())) {
            throw new GroupException(GroupExceptionEnum.INVALID_GROUP_NAME);
        }
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        List<Integer> friendIds = new ArrayList<>();
        createGroupDto.getFriendIds().forEach(id -> {
            if (id != null && sysUserFriendMapper.isOneWayFriend(id, userVo.getId()))
                friendIds.add(id);
        });
        if (friendIds.isEmpty()) {
            log.debug("群聊邀请人数为空");
            return false;
        }
        if (sysGroupMemberMapper.countGroupsByUserRole(userVo.getId(), UserRole.LEADER) >= groupProperties.getMaxGroupPerUser()) {
            throw new GroupException(GroupExceptionEnum.CREATABLE_GROUP_LIMIT_EXCEEDED);
        }
        // 创建群聊
        SysGroup group = new SysGroup();
        group.setGroupName(createGroupDto.getGroupName());
        group.setAvatar("/" + groupProperties.getAvatarDir() + "/" + groupProperties.getDefaultAvatarName());
        baseMapper.insert(group);
        // 设置群主
        SysGroupMember sysGroupMember = new SysGroupMember();
        sysGroupMember.setGroupId(group.getGroupId());
        sysGroupMember.setUserId(userVo.getId());
        sysGroupMember.setRole(UserRole.LEADER);
        sysGroupMember.setMuted(false);
        sysGroupMemberMapper.insert(sysGroupMember);
        // 发送邀请
        friendIds.forEach(id -> {
            SysGroupInvitation invitation = new SysGroupInvitation();
            invitation.setGroupId(group.getGroupId());
            invitation.setInvitedBy(userVo.getId());
            invitation.setInvitedUserId(id);
            invitation.setStatus(GroupInvitationStatus.PENDING);
            sysGroupInvitationMapper.insert(invitation);
        });
        // 讲群聊加入到用户会话中
        sysUserConversationService.setConversationRead(userVo.getId(), group.getGroupId(), ChatType.GROUP);
        return true;
    }

    @Override
    @NotNull
    public List<UserGroupVo> getUserGroupList(int pageNum) {
        if (pageNum <= 0) return List.of();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null)
            return List.of();
        IPage<UserGroupVo> ipage = baseMapper.selectUserGroupList(new Page<>(pageNum, 10), userVo.getId());
        return ipage.getRecords();
    }
}




