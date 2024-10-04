package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.th1nk.easychat.domain.SysGroupInvitation;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.vo.GroupMemberInfoVo;
import top.th1nk.easychat.domain.vo.GroupMemberVo;
import top.th1nk.easychat.enums.GroupInvitationStatus;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupInvitationMapper;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.SysGroupMemberService;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member】的数据库操作Service实现
 * @createDate 2024-08-27 22:20:11
 */
@Service
public class SysGroupMemberServiceImpl extends ServiceImpl<SysGroupMemberMapper, SysGroupMember>
        implements SysGroupMemberService {
    @Resource
    private ConversationRedisService conversationRedisService;
    @Resource
    private SysUserConversationMapper sysUserConversationMapper;
    @Resource
    private SysGroupInvitationMapper sysGroupInvitationMapper;

    @Override
    public List<GroupMemberVo> getGroupMembers(int groupId, int pageNum) {
        return baseMapper.selectGroupMemberVo(new Page<>(pageNum, 10), groupId);
    }

    @Override
    public GroupMemberInfoVo getGroupMemberInfo(int userId, int groupId) {
        if (userId == 0 || groupId == 0) {
            return null;
        }
        return baseMapper.selectGroupMemberInfoVo(userId, groupId);
    }

    @Override
    @CacheEvict(cacheNames = "user:perms", key = "#userId", condition = "#result==true")
    @Transactional
    public boolean quitGroup(int userId, int groupId) {
        LambdaQueryWrapper<SysGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysGroupMember::getUserId, userId)
                .eq(SysGroupMember::getGroupId, groupId);
        SysGroupMember sysGroupMember = baseMapper.selectOne(queryWrapper);
        // 群主不能退群
        if (sysGroupMember != null && sysGroupMember.getRole() == UserRole.LEADER)
            throw new GroupException(GroupExceptionEnum.LEADER_CANNOT_QUIT);
        if (baseMapper.delete(queryWrapper) == 0)
            return false;
        conversationRedisService.deleteConversation(userId, groupId, ChatType.GROUP);
        sysUserConversationMapper.deleteConversation(userId, groupId, ChatType.GROUP);
        // 添加一条退群记录
        SysGroupInvitation sysGroupInvitation = new SysGroupInvitation();
        sysGroupInvitation.setGroupId(groupId);
        sysGroupInvitation.setStatus(GroupInvitationStatus.QUITED);
        sysGroupInvitation.setInvitedUserId(userId);
        sysGroupInvitationMapper.insert(sysGroupInvitation);
        return true;
    }

    @CacheEvict(cacheNames = "user:perms", key = "#memberId", condition = "#result==true")
    @Override
    public boolean kickMember(int userId, int groupId, int memberId) {
        LambdaQueryWrapper<SysGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysGroupMember::getUserId, memberId)
                .eq(SysGroupMember::getGroupId, groupId);
        SysGroupMember sysGroupMember = baseMapper.selectOne(queryWrapper);
        if (sysGroupMember != null && sysGroupMember.getRole() == UserRole.LEADER)
            throw new GroupException(GroupExceptionEnum.LEADER_CANNOT_BE_KICKED);
        if (baseMapper.delete(queryWrapper) == 0)
            return false;
        conversationRedisService.deleteConversation(memberId, groupId, ChatType.GROUP);
        sysUserConversationMapper.deleteConversation(memberId, groupId, ChatType.GROUP);
        // 添加一条踢人记录
        SysGroupInvitation sysGroupInvitation = new SysGroupInvitation();
        sysGroupInvitation.setGroupId(groupId);
        sysGroupInvitation.setStatus(GroupInvitationStatus.KICKED);
        sysGroupInvitation.setInvitedUserId(memberId);
        sysGroupInvitation.setInvitedBy(userId);
        sysGroupInvitationMapper.insert(sysGroupInvitation);
        return true;
    }
}




