package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.SysGroupNotification;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.domain.vo.GroupNotificationVo;
import top.th1nk.easychat.enums.GroupNotificationType;
import top.th1nk.easychat.enums.GroupStatus;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupMapper;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysGroupNotificationMapper;
import top.th1nk.easychat.service.SysGroupNotificationService;
import top.th1nk.easychat.service.WebSocketService;
import top.th1nk.easychat.utils.SecurityUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @author vinka
 * @description 针对表【ec_group_invitation】的数据库操作Service实现
 * @createDate 2024-08-27 22:20:11
 */
@Slf4j
@Service
public class SysGroupNotificationServiceImpl extends ServiceImpl<SysGroupNotificationMapper, SysGroupNotification>
        implements SysGroupNotificationService {
    /**
     * 邀请过期时间，单位：小时
     */
    private static final int INVITATION_EXPIRE_TIME = 24 * 7;

    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private SysGroupMapper sysGroupMapper;
    @Resource
    private SecurityUtils securityUtils;

    private boolean insertGroupMember(int userId, int groupId) {
        SysGroupMember sysGroupMember = new SysGroupMember();
        sysGroupMember.setUserId(userId);
        sysGroupMember.setGroupId(groupId);
        sysGroupMember.setMuted(false);
        sysGroupMember.setRole(UserRole.USER);
        return sysGroupMemberMapper.insert(sysGroupMember) > 0;
    }

    @Override
    public int refreshAllInvitationStatus() {
        log.debug("刷新所有群组邀请状态");
        LambdaUpdateWrapper<SysGroupNotification> qw = new LambdaUpdateWrapper<>();
        qw.lt(SysGroupNotification::getCreateTime, LocalDateTime.now().minusHours(INVITATION_EXPIRE_TIME))
                .eq(SysGroupNotification::getType, GroupNotificationType.PENDING)
                .set(SysGroupNotification::getType, GroupNotificationType.EXPIRED);
        return baseMapper.update(qw);
    }

    @Override
    public List<GroupNotificationVo> getUserGroupNotificationList(int userId, int page) {
        if (page <= 0) return List.of();
        log.debug("获取用户群聊通知列表,userId:{},page:{}", userId, page);
        return baseMapper.selectNotificationVoByUserId(new Page<>(page, 10), userId);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "user:perms", key = "#userId")
    public boolean userAcceptInvitation(int userId, int groupId) {
        if (sysGroupMemberMapper.selectByUserIdAndGroupId(userId, groupId) != null)
            throw new GroupException(GroupExceptionEnum.ALREADY_IN_GROUP);
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("用户接受群聊邀请,userId:{},groupId:{}", userId, groupId);
        LambdaUpdateWrapper<SysGroupNotification> qw = new LambdaUpdateWrapper<>();
        qw.eq(SysGroupNotification::getGroupId, groupId)
                .eq(SysGroupNotification::getTargetId, userId)
                .eq(SysGroupNotification::getType, GroupNotificationType.PENDING);
        SysGroupNotification invitation = baseMapper.selectOne(qw);
        if (invitation == null) return false;
        SysGroupMember invitedByUser = sysGroupMemberMapper.selectByUserIdAndGroupId(invitation.getOperatorId(), groupId);
        if (invitedByUser.getRole() == UserRole.LEADER || invitedByUser.getRole() == UserRole.ADMIN) {
            // 邀请人是管理员
            if (baseMapper.update(null, qw.set(SysGroupNotification::getType, GroupNotificationType.ADMIN_ACCEPTED)) == 0)
                return false;
            // 添加群组成员
            if (this.insertGroupMember(userId, groupId)) {
                webSocketService.publishMessage(WSMessage.command(groupId, ChatType.GROUP,
                        MessageCommand.GROUP_INVITED,
                        List.of(String.valueOf(invitation.getOperatorId()), String.valueOf(invitation.getTargetId()))));
                return true;
            } else throw new GroupException(GroupExceptionEnum.GROUP_MEMBER_CREATE_FAIL);
        } else
            return baseMapper.update(null, qw.set(SysGroupNotification::getType, GroupNotificationType.ADMIN_PENDING)) > 0;
    }

    @Override
    public boolean userRejectInvitation(int userId, int groupId) {
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("用户拒绝群聊邀请,userId:{},groupId:{}", userId, groupId);
        LambdaUpdateWrapper<SysGroupNotification> qw = new LambdaUpdateWrapper<>();
        qw.eq(SysGroupNotification::getGroupId, groupId)
                .eq(SysGroupNotification::getTargetId, userId)
                .eq(SysGroupNotification::getType, GroupNotificationType.PENDING);
        if (baseMapper.selectOne(qw) == null) return false;
        return baseMapper.update(null, qw.set(SysGroupNotification::getType, GroupNotificationType.REJECTED)) > 0;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "user:perms", key = "#userId")
    public boolean adminAcceptInvitation(int userId, int groupId) {
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("管理员同意用户的进群邀请,userId:{},groupId:{}", userId, groupId);
        // 获取邀请详细信息
        LambdaUpdateWrapper<SysGroupNotification> qw = new LambdaUpdateWrapper<>();
        qw.eq(SysGroupNotification::getGroupId, groupId)
                .eq(SysGroupNotification::getTargetId, userId)
                .eq(SysGroupNotification::getType, GroupNotificationType.ADMIN_PENDING);
        SysGroupNotification invitation = baseMapper.selectOne(qw);
        if (invitation == null) throw new GroupException(GroupExceptionEnum.INVITATION_NOT_FOUND);
        invitation.setType(GroupNotificationType.ADMIN_ACCEPTED);
        if (baseMapper.updateById(invitation) == 0)
            return false;
        // 添加群组成员
        if (this.insertGroupMember(userId, groupId)) {
            webSocketService.publishMessage(WSMessage.command(groupId, ChatType.GROUP,
                    MessageCommand.GROUP_INVITED,
                    List.of(String.valueOf(invitation.getOperatorId()), String.valueOf(invitation.getTargetId()))));
            return true;
        } else throw new GroupException(GroupExceptionEnum.INSERT_GROUP_MEMBER_FAIL);
    }

    @Override
    public boolean adminRejectInvitation(int userId, int groupId) {
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("管理员拒绝用户的进群邀请,userId:{},groupId:{}", userId, groupId);
        return baseMapper.update(null, new LambdaUpdateWrapper<SysGroupNotification>()
                .eq(SysGroupNotification::getGroupId, groupId)
                .eq(SysGroupNotification::getTargetId, userId)
                .eq(SysGroupNotification::getType, GroupNotificationType.ADMIN_PENDING)
                .set(SysGroupNotification::getType, GroupNotificationType.ADMIN_REJECTED)) > 0;
    }

    @Override
    @Transactional
    public boolean inviteMembers(int userId, int groupId, List<Integer> memberIds) {
        if (memberIds == null || memberIds.isEmpty() || memberIds.size() > 20) return false;
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        Set<String> permissions = securityUtils.getPermissions(userId);
        log.debug("邀请用户加入群聊,userId:{},groupId:{},memberIds:{}", userId, groupId, memberIds);
        memberIds
                .stream()
                .filter((memberId) ->
                        permissions.contains("FRIEND:" + memberId)
                                && !securityUtils.getPermissions(memberId).contains("GROUP:" + groupId)
                ).forEach((id) -> {
                    SysGroupNotification invitation = new SysGroupNotification();
                    invitation.setGroupId(groupId);
                    invitation.setOperatorId(userId);
                    invitation.setTargetId(id);
                    invitation.setType(GroupNotificationType.PENDING);
                    LambdaQueryWrapper<SysGroupNotification> qw = new LambdaQueryWrapper<>();
                    qw.eq(SysGroupNotification::getGroupId, groupId)
                            .eq(SysGroupNotification::getOperatorId, userId)
                            .eq(SysGroupNotification::getTargetId, id)
                            .nested(s -> s.eq(SysGroupNotification::getType, GroupNotificationType.PENDING)
                                    .or()
                                    .eq(SysGroupNotification::getType, GroupNotificationType.ADMIN_PENDING));
                    if (baseMapper.selectOne(qw) == null)
                        baseMapper.insert(invitation);
                });
        return true;
    }
}




