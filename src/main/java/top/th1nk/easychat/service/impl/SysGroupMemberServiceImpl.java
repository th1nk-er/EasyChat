package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.vo.GroupMemberInfoVo;
import top.th1nk.easychat.domain.vo.GroupMemberVo;
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
    public boolean quitGroup(int userId, int groupId) {
        GroupMemberInfoVo groupMemberInfo = this.getGroupMemberInfo(userId, groupId);
        if (groupMemberInfo == null) {
            return false;
        }
        LambdaQueryWrapper<SysGroupMember> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysGroupMember::getUserId, userId)
                .eq(SysGroupMember::getGroupId, groupId);
        conversationRedisService.deleteConversation(userId, groupId, ChatType.GROUP);
        sysUserConversationMapper.deleteConversation(userId, groupId, ChatType.GROUP);
        return baseMapper.delete(queryWrapper) > 0;
    }
}




