package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupMemberIgnored;
import top.th1nk.easychat.mapper.SysGroupMemberIgnoredMapper;
import top.th1nk.easychat.service.SysGroupMemberIgnoredService;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member_muted】的数据库操作Service实现
 * @createDate 2024-10-20 18:46:12
 */
@Service
public class SysGroupMemberIgnoredServiceImpl extends ServiceImpl<SysGroupMemberIgnoredMapper, SysGroupMemberIgnored>
        implements SysGroupMemberIgnoredService {

    @Override
    public boolean ignoreMemberForUser(int userId, int groupId, int ignoredId) {
        if (userId <= 0 || groupId <= 0 || ignoredId <= 0)
            return false;
        if (userId == ignoredId)
            return false;
        SysGroupMemberIgnored sysGroupMemberIgnored = new SysGroupMemberIgnored();
        sysGroupMemberIgnored.setUserId(userId);
        sysGroupMemberIgnored.setGroupId(groupId);
        sysGroupMemberIgnored.setIgnored(true);
        sysGroupMemberIgnored.setIgnoredId(ignoredId);
        return baseMapper.insertOrUpdate(sysGroupMemberIgnored);
    }

    @Override
    public boolean cancelIgnoreMemberForUser(int userId, int groupId, int ignoredId) {
        if (userId <= 0 || groupId <= 0 || ignoredId <= 0)
            return false;
        if (userId == ignoredId)
            return false;
        LambdaUpdateWrapper<SysGroupMemberIgnored> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysGroupMemberIgnored::getUserId, userId)
                .eq(SysGroupMemberIgnored::getGroupId, groupId)
                .eq(SysGroupMemberIgnored::getIgnoredId, ignoredId)
                .set(SysGroupMemberIgnored::isIgnored, false);
        return baseMapper.update(wrapper) > 0;
    }

    @Override
    public List<Integer> getIgnoredMemberIds(int userId, int groupId) {
        if (userId <= 0 || groupId <= 0)
            return List.of();
        LambdaQueryWrapper<SysGroupMemberIgnored> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysGroupMemberIgnored::getUserId, userId)
                .eq(SysGroupMemberIgnored::getGroupId, groupId)
                .eq(SysGroupMemberIgnored::isIgnored, true);
        return baseMapper.selectList(wrapper).stream().map(SysGroupMemberIgnored::getIgnoredId).toList();
    }
}




