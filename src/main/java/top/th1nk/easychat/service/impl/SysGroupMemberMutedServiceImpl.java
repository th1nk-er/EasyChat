package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupMemberMuted;
import top.th1nk.easychat.mapper.SysGroupMemberMutedMapper;
import top.th1nk.easychat.service.SysGroupMemberMutedService;

import java.time.LocalDateTime;

/**
 * @author vinka
 * @description 针对表【ec_group_member_muted】的数据库操作Service实现
 * @createDate 2024-11-04 14:06:48
 */
@Service
public class SysGroupMemberMutedServiceImpl extends ServiceImpl<SysGroupMemberMutedMapper, SysGroupMemberMuted>
        implements SysGroupMemberMutedService {

    private static final int MAX_MUTE_DURATION = 60 * 24 * 30;

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
        sysGroupMemberMuted.setMuted(true);
        sysGroupMemberMuted.setMuteTime(LocalDateTime.now());
        sysGroupMemberMuted.setUnmuteTime(LocalDateTime.now().plusMinutes(duration));
        return save(sysGroupMemberMuted);
    }

    @Override
    public boolean unmuteMember(int groupId, int memberId) {
        if (groupId <= 0 || memberId <= 0) {
            return false;
        }
        return lambdaUpdate()
                .eq(SysGroupMemberMuted::getGroupId, groupId)
                .eq(SysGroupMemberMuted::getUserId, memberId)
                .set(SysGroupMemberMuted::isMuted, false)
                .set(SysGroupMemberMuted::getUnmuteTime, LocalDateTime.now())
                .update();
    }
}




