package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.MessageType;
import top.th1nk.easychat.domain.vo.UserConversationVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.SysUserConversationService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_chat_history】的数据库操作Service实现
 * @createDate 2024-08-01 18:08:50
 */
@Service
@Slf4j
public class SysUserConversationServiceImpl extends ServiceImpl<SysUserConversationMapper, SysUserConversation>
        implements SysUserConversationService {

    @Resource
    private JwtUtils jwtUtils;

    @Resource
    private ConversationRedisService conversationRedisService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserFriendMapper sysUserFriendMapper;

    private UserConversationVo transformToVo(SysUserConversation sysUserConversation) {
        UserConversationVo vo = new UserConversationVo();
        BeanUtils.copyProperties(sysUserConversation, vo);
        if (sysUserConversation.getFriendId() != null) {
            SysUser sysUser = sysUserMapper.selectById(sysUserConversation.getFriendId());
            vo.setAvatar(sysUser.getAvatar());
            vo.setNickname(sysUser.getNickname());
            UserFriendVo userFriendVo = sysUserFriendMapper.selectUserFriendVo(sysUserConversation.getUid(), sysUserConversation.getFriendId());
            if (userFriendVo != null) {
                vo.setRemark(userFriendVo.getRemark());
                vo.setMuted(userFriendVo.isMuted());
            }
        }
        return vo;
    }

    private List<UserConversationVo> transformToVo(List<SysUserConversation> sysUserConversations) {
        List<UserConversationVo> result = new ArrayList<>();
        for (SysUserConversation sysUserConversation : sysUserConversations) {
            result.add(transformToVo(sysUserConversation));
        }
        return result;
    }

    @Override
    public List<UserConversationVo> getConversations(int pageNum) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        // 查询用户聊天列表
        log.debug("查询用户聊天列表 用户ID:{} 页码:{}", userVo.getId(), pageNum);
        LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserConversation::getUid, userVo.getId())
                .orderByAsc(SysUserConversation::getUpdateTime);
        Page<SysUserConversation> pages = baseMapper.selectPage(new Page<>(pageNum, 15), qw);
        List<SysUserConversation> redisHistory;
        // 仅当页码为1时，将redis数据一并返回
        if (pageNum == 1)
            redisHistory = conversationRedisService.getUserConversations(userVo.getId());
        else
            redisHistory = List.of();
        if (pages.getRecords().isEmpty()) return transformToVo(redisHistory);
        if (redisHistory.isEmpty()) return transformToVo(pages.getRecords());
        List<UserConversationVo> result = new ArrayList<>();
        // 将redis中较新数据替换数据库中数据
        for (SysUserConversation redisRecord : redisHistory) {
            for (SysUserConversation pageRecord : pages.getRecords()) {
                if (redisRecord.getFriendId() != null && redisRecord.getFriendId().equals(pageRecord.getFriendId())) {
                    result.add(transformToVo(redisRecord));
                } else if (redisRecord.getGroupId() != null && redisRecord.getGroupId().equals(pageRecord.getGroupId())) {
                    result.add(transformToVo(redisRecord));
                } else {
                    result.add(transformToVo(pageRecord));
                }
            }
        }
        return result;
    }

    @Override
    public void setConversationRead(int userId, int receiverId) {
        log.debug("设置用户对话为已读 userId:{} receiverId:{}", userId, receiverId);
        if (conversationRedisService.setConversationRead(userId, receiverId))
            return;
        // 在数据库中设为已读
        LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserConversation::getUid, userId)
                .eq(SysUserConversation::getFriendId, receiverId);
        SysUserConversation conversation = baseMapper.selectOne(qw);
        if (conversation != null) {
            conversation.setUnreadCount(0);
            baseMapper.updateById(conversation);
        } else {
            // 保存新会话
            conversation = new SysUserConversation();
            conversation.setUpdateTime(LocalDateTime.now());
            conversation.setMessageType(MessageType.TEXT);
            conversation.setUid(userId);
            conversation.setFriendId(receiverId);
            conversation.setUnreadCount(0);
            baseMapper.insert(conversation);
        }
    }

    @Override
    public void loadConversationToRedis() {
        // 仅加载未读对话列表
        log.info("加载用户对话列表 ...");
        LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
        qw.gt(SysUserConversation::getUnreadCount, 0);
        List<SysUserConversation> sysUserConversations = baseMapper.selectList(qw);
        conversationRedisService.addToConversation(sysUserConversations);
        log.info("加载用户对话列表完成");
    }
}




