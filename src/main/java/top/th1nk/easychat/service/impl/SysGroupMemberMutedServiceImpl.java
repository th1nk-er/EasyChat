package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupMemberMuted;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.domain.vo.GroupMemberMuteVo;
import top.th1nk.easychat.mapper.SysGroupMemberMutedMapper;
import top.th1nk.easychat.service.SysGroupMemberMutedService;
import top.th1nk.easychat.service.WebSocketService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member_muted】的数据库操作Service实现
 * @createDate 2024-11-04 14:06:48
 */
@Slf4j
@Service
public class SysGroupMemberMutedServiceImpl extends ServiceImpl<SysGroupMemberMutedMapper, SysGroupMemberMuted>
        implements SysGroupMemberMutedService {

    private static final int MAX_MUTE_DURATION = 60 * 24 * 30;
    @Resource
    private WebSocketService webSocketService;

    @Override
    public boolean muteMember(int groupId, int memberId, int adminId, int duration) {
        if (groupId <= 0 || memberId <= 0 || adminId <= 0 || duration <= 0 || duration > MAX_MUTE_DURATION) {
            return false;
        }
        SysGroupMemberMuted sysGroupMemberMuted = lambdaQuery().eq(SysGroupMemberMuted::getGroupId, groupId).eq(SysGroupMemberMuted::getUserId, memberId).one();
        if (sysGroupMemberMuted == null) {
            sysGroupMemberMuted = new SysGroupMemberMuted();
            sysGroupMemberMuted.setGroupId(groupId);
            sysGroupMemberMuted.setUserId(memberId);
            sysGroupMemberMuted.setAdminId(adminId);
        } else if (sysGroupMemberMuted.isMuted()) {
            return false;
        }
        log.debug("禁言群成员 群聊ID:{} 管理员ID:{} 成员ID:{} 禁言时长:{}", groupId, adminId, memberId, duration);
        sysGroupMemberMuted.setMuted(true);
        sysGroupMemberMuted.setMuteTime(LocalDateTime.now());
        sysGroupMemberMuted.setUnmuteTime(LocalDateTime.now().plusMinutes(duration));
        if (saveOrUpdate(sysGroupMemberMuted)) {
            webSocketService.publishMessage(WSMessage.command(groupId,
                    ChatType.GROUP,
                    MessageCommand.MEMBER_MUTED,
                    List.of(String.valueOf(adminId), String.valueOf(memberId), String.valueOf(duration))));
            return true;
        }
        return false;
    }

    @Override
    public boolean unmuteMember(int groupId, int memberId, int adminId) {
        if (groupId <= 0 || memberId <= 0 || adminId <= 0) {
            return false;
        }
        log.debug("解除禁言群成员 群聊ID:{} 管理员ID:{} 成员ID:{}", groupId, adminId, memberId);
        if (lambdaUpdate()
                .eq(SysGroupMemberMuted::getGroupId, groupId)
                .eq(SysGroupMemberMuted::getUserId, memberId)
                .set(SysGroupMemberMuted::isMuted, false)
                .set(SysGroupMemberMuted::getUnmuteTime, LocalDateTime.now())
                .update()) {
            webSocketService.publishMessage(WSMessage.command(groupId,
                    ChatType.GROUP,
                    MessageCommand.MEMBER_CANCEL_MUTE,
                    List.of(String.valueOf(adminId), String.valueOf(memberId))));
            return true;
        }
        return false;
    }

    @Override
    public GroupMemberMuteVo getMuteInfo(int groupId, int memberId) {
        if (groupId <= 0 || memberId <= 0) {
            return null;
        }
        SysGroupMemberMuted sysGroupMemberMuted = baseMapper.selectByGroupIdAndUserId(groupId, memberId);
        if (sysGroupMemberMuted == null) return null;
        GroupMemberMuteVo vo = new GroupMemberMuteVo();
        BeanUtils.copyProperties(sysGroupMemberMuted, vo);
        return vo;
    }

    @Override
    public List<GroupMemberMuteVo> getMuteInfoList(int groupId) {
        if (groupId <= 0) {
            return List.of();
        }
        return lambdaQuery().eq(SysGroupMemberMuted::getGroupId, groupId).list().stream().map(entity -> {
            GroupMemberMuteVo vo = new GroupMemberMuteVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).toList();
    }
}




