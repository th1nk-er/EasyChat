package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.MessageType;
import top.th1nk.easychat.domain.vo.UserConversationVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;
import top.th1nk.easychat.mapper.SysGroupMapper;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.SysUserConversationService;
import top.th1nk.easychat.utils.SecurityUtils;

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
    private ConversationRedisService conversationRedisService;
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserFriendMapper sysUserFriendMapper;
    @Resource
    private SysGroupMapper sysGroupMapper;
    @Resource
    private SecurityUtils securityUtils;

    private UserConversationVo transformToVo(SysUserConversation sysUserConversation) {
        UserConversationVo vo = new UserConversationVo();
        BeanUtils.copyProperties(sysUserConversation, vo);
        if (sysUserConversation.getChatId() != null) {
            if (sysUserConversation.getChatType() == ChatType.FRIEND) {
                SysUser sysUser = sysUserMapper.selectById(sysUserConversation.getChatId());
                if (sysUser == null) return vo;
                vo.setAvatar(sysUser.getAvatar());
                vo.setNickname(sysUser.getNickname());
                UserFriendVo userFriendVo = sysUserFriendMapper.selectUserFriendVo(sysUserConversation.getUid(), sysUserConversation.getChatId());
                if (userFriendVo != null) {
                    vo.setRemark(userFriendVo.getRemark());
                    vo.setMuted(userFriendVo.isMuted());
                }
            } else if (sysUserConversation.getChatType() == ChatType.GROUP) {
                // 群组
                UserGroupVo userGroupVo = sysGroupMapper.selectUserGroupVo(sysUserConversation.getUid(), sysUserConversation.getChatId());
                if (userGroupVo == null) return vo;
                vo.setNickname(userGroupVo.getGroupName());
                vo.setMuted(userGroupVo.isMuted());
                vo.setRemark(userGroupVo.getGroupRemark());
                vo.setAvatar(userGroupVo.getAvatar());
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
    public List<UserConversationVo> getUserConversations(int userId) {
        // 查询用户聊天列表
        log.debug("查询用户聊天列表 用户ID:{}", userId);
        List<SysUserConversation> redisRecords = conversationRedisService.getUserConversations(userId);
        if (redisRecords.isEmpty()) {
            LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
            qw.eq(SysUserConversation::getUid, userId)
                    .orderByDesc(SysUserConversation::getUpdateTime);
            List<SysUserConversation> dbRecords = baseMapper.selectList(qw);
            conversationRedisService.addToConversation(dbRecords);
            return transformToVo(dbRecords);
        } else {
            return transformToVo(redisRecords);
        }
    }

    @Override
    public void setConversationRead(int userId, int chatId, ChatType chatType) {
        if (chatType == ChatType.FRIEND && userId == chatId) return;
        if (chatType == ChatType.FRIEND) {
            if (!securityUtils.getPermissions(userId).contains("FRIEND:" + chatId)) return;
        } else if (chatType == ChatType.GROUP) {
            if (!securityUtils.getPermissions(userId).contains("GROUP:" + chatId)) return;
        }
        log.debug("设置用户对话为已读 userId:{} receiverId:{} chatType:{}", userId, chatId, chatType);
        if (conversationRedisService.setConversationRead(userId, chatId, chatType))
            return;
        // 保存新会话
        SysUserConversation conversation = new SysUserConversation();
        conversation.setUpdateTime(LocalDateTime.now());
        conversation.setMessageType(MessageType.TEXT);
        conversation.setUid(userId);
        conversation.setChatId(chatId);
        conversation.setUnreadCount(0);
        conversation.setChatType(chatType);
        conversationRedisService.addToConversation(List.of(conversation));
    }
}