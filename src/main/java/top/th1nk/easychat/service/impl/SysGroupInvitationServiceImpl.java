package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupInvitation;
import top.th1nk.easychat.domain.vo.GroupInvitationVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.GroupInvitationStatus;
import top.th1nk.easychat.mapper.SysGroupInvitationMapper;
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


    @Override
    public int refreshAllInvitationStatus() {
        LambdaUpdateWrapper<SysGroupInvitation> qw = new LambdaUpdateWrapper<>();
        qw.gt(SysGroupInvitation::getCreateTime, LocalDateTime.now().plusHours(INVITATION_EXPIRE_TIME))
                .set(SysGroupInvitation::getStatus, GroupInvitationStatus.EXPIRED);
        return baseMapper.update(qw);
    }

    @Override
    public List<GroupInvitationVo> getUserGroupInvitationList(int page) {
        if (page <= 0) return List.of();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        return baseMapper.selectInvitationVoByUserId(new Page<>(page, 10), userVo.getId());
    }
}




