package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.th1nk.easychat.domain.SysGroupInvitation;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.vo.GroupAdminInvitationVo;
import top.th1nk.easychat.domain.vo.GroupInvitationVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.GroupInvitationStatus;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupInvitationMapper;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.service.SysGroupInvitationService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_invitation】的数据库操作Service实现
 * @createDate 2024-08-27 22:20:11
 */
@Service
public class SysGroupInvitationServiceImpl extends ServiceImpl<SysGroupInvitationMapper, SysGroupInvitation>
        implements SysGroupInvitationService {
    /**
     * 邀请过期时间，单位：小时
     */
    private static final int INVITATION_EXPIRE_TIME = 24 * 7;

    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;


    @Override
    public int refreshAllInvitationStatus() {
        LambdaUpdateWrapper<SysGroupInvitation> qw = new LambdaUpdateWrapper<>();
        qw.lt(SysGroupInvitation::getCreateTime, LocalDateTime.now().minusHours(INVITATION_EXPIRE_TIME))
                .eq(SysGroupInvitation::getStatus, GroupInvitationStatus.PENDING)
                .set(SysGroupInvitation::getStatus, GroupInvitationStatus.EXPIRED);
        return baseMapper.update(qw);
    }

    @Override
    public List<GroupInvitationVo> getUserGroupInvitationList(int userId, int page) {
        if (page <= 0) return List.of();
        return baseMapper.selectInvitationVoByUserId(new Page<>(page, 10), userId);
    }

    @Override
    public List<GroupAdminInvitationVo> getAdminGroupInvitationList(int page) {
        if (page <= 0) return List.of();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        return baseMapper.selectAdminInvitationVoByUserId(new Page<>(page, 10), userVo.getId());
    }

    @Override
    @Transactional
    public boolean userAcceptInvitation(int groupId) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        if (sysGroupMemberMapper.selectByUserIdAndGroupId(userVo.getId(), groupId) != null)
            throw new GroupException(GroupExceptionEnum.ALREADY_IN_GROUP);
        LambdaUpdateWrapper<SysGroupInvitation> qw = new LambdaUpdateWrapper<>();
        qw.eq(SysGroupInvitation::getGroupId, groupId)
                .eq(SysGroupInvitation::getInvitedUserId, userVo.getId())
                .eq(SysGroupInvitation::getStatus, GroupInvitationStatus.PENDING);
        SysGroupInvitation invitation = baseMapper.selectOne(qw);
        if (invitation == null) return false;
        SysGroupMember invitedByUser = sysGroupMemberMapper.selectByUserIdAndGroupId(invitation.getInvitedBy(), groupId);
        if (invitedByUser.getRole() == UserRole.LEADER || invitedByUser.getRole() == UserRole.ADMIN) {
            // 邀请人是管理员
            if (baseMapper.update(null, qw.set(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_ACCEPTED)) == 0)
                return false;
            // 添加群组成员
            SysGroupMember sysGroupMember = new SysGroupMember();
            sysGroupMember.setUserId(userVo.getId());
            sysGroupMember.setGroupId(groupId);
            sysGroupMember.setMuted(false);
            sysGroupMember.setRole(UserRole.USER);
            if (sysGroupMemberMapper.insert(sysGroupMember) == 0)
                throw new GroupException(GroupExceptionEnum.GROUP_MEMBER_CREATE_FAIL);
            return true;
        } else
            return baseMapper.update(null, qw.set(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_PENDING)) > 0;
    }

    @Override
    public boolean userRejectInvitation(int groupId) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        LambdaUpdateWrapper<SysGroupInvitation> qw = new LambdaUpdateWrapper<>();
        qw.eq(SysGroupInvitation::getGroupId, groupId)
                .eq(SysGroupInvitation::getInvitedUserId, userVo.getId())
                .eq(SysGroupInvitation::getStatus, GroupInvitationStatus.PENDING);
        if (baseMapper.selectOne(qw) == null) return false;
        return baseMapper.update(null, qw.set(SysGroupInvitation::getStatus, GroupInvitationStatus.REJECTED)) > 0;
    }

    @Override
    @Transactional
    public boolean adminAcceptInvitation(int userId, int groupId) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        SysGroupMember adminMember = sysGroupMemberMapper.selectByUserIdAndGroupId(userVo.getId(), groupId);
        if (adminMember.getRole() != UserRole.LEADER && adminMember.getRole() != UserRole.ADMIN)
            throw new GroupException(GroupExceptionEnum.NOT_ADMIN);
        if (baseMapper.update(null, new LambdaUpdateWrapper<SysGroupInvitation>()
                .eq(SysGroupInvitation::getGroupId, groupId)
                .eq(SysGroupInvitation::getInvitedUserId, userId)
                .eq(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_PENDING)
                .set(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_ACCEPTED)) == 0)
            return false;
        // 添加群组成员
        SysGroupMember sysGroupMember = new SysGroupMember();
        sysGroupMember.setUserId(userId);
        sysGroupMember.setGroupId(groupId);
        sysGroupMember.setMuted(false);
        sysGroupMember.setRole(UserRole.USER);
        if (sysGroupMemberMapper.insert(sysGroupMember) == 0)
            throw new GroupException(GroupExceptionEnum.GROUP_MEMBER_CREATE_FAIL);
        return true;
    }

    @Override
    public boolean adminRejectInvitation(int userId, int groupId) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        SysGroupMember adminMember = sysGroupMemberMapper.selectByUserIdAndGroupId(userVo.getId(), groupId);
        if (adminMember.getRole() != UserRole.LEADER && adminMember.getRole() != UserRole.ADMIN)
            throw new GroupException(GroupExceptionEnum.NOT_ADMIN);
        return baseMapper.update(null, new LambdaUpdateWrapper<SysGroupInvitation>()
                .eq(SysGroupInvitation::getGroupId, groupId)
                .eq(SysGroupInvitation::getInvitedUserId, userId)
                .eq(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_PENDING)
                .set(SysGroupInvitation::getStatus, GroupInvitationStatus.ADMIN_REJECTED)) > 0;
    }
}




