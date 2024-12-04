package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.SysGroupMemberIgnored;
import top.th1nk.easychat.domain.vo.GroupMemberIgnoredVo;
import top.th1nk.easychat.enums.GroupStatus;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupMapper;
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
    @Resource
    private SysGroupMapper sysGroupMapper;

    @Override
    public boolean ignoreMemberForUser(int userId, int groupId, int ignoredId) {
        if (userId <= 0 || groupId <= 0 || ignoredId <= 0)
            return false;
        if (userId == ignoredId)
            return false;
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null) return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
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
        SysGroup sysGroup = sysGroupMapper.selectById(groupId);
        if (sysGroup == null) return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        LambdaUpdateWrapper<SysGroupMemberIgnored> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SysGroupMemberIgnored::getUserId, userId)
                .eq(SysGroupMemberIgnored::getGroupId, groupId)
                .eq(SysGroupMemberIgnored::getIgnoredId, ignoredId)
                .set(SysGroupMemberIgnored::isIgnored, false);
        return baseMapper.update(wrapper) > 0;
    }

    @Override
    public List<GroupMemberIgnoredVo> getGroupIgnoredVo(int userId, int groupId) {
        if (userId <= 0 || groupId <= 0)
            return List.of();
        LambdaQueryWrapper<SysGroupMemberIgnored> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysGroupMemberIgnored::getUserId, userId)
                .eq(SysGroupMemberIgnored::getGroupId, groupId)
                .eq(SysGroupMemberIgnored::isIgnored, true);
        return baseMapper.selectList(wrapper).stream().map(m -> {
            GroupMemberIgnoredVo vo = new GroupMemberIgnoredVo();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).toList();
    }
}




