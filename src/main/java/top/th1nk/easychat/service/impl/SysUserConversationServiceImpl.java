package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUserConversation;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.SysUserConversationService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

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

    @Override
    public List<SysUserConversation> getChatHistory(int pageNum) {
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return List.of();
        // 查询用户聊天列表
        log.debug("查询用户聊天列表 用户ID:{} 页码:{}", userVo.getId(), pageNum);
        LambdaQueryWrapper<SysUserConversation> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserConversation::getUid, userVo.getId())
                .orderByAsc(SysUserConversation::getUpdateTime);
        Page<SysUserConversation> pages = baseMapper.selectPage(new Page<>(pageNum, 15), qw);
        List<SysUserConversation> redisHistory = conversationRedisService.getUserConversations(userVo.getId());
        if (pages.getRecords().isEmpty()) return redisHistory;
        if (redisHistory.isEmpty()) return pages.getRecords();
        List<SysUserConversation> result = new ArrayList<>();
        // 将redis中较新数据替换数据库中数据
        for (SysUserConversation redisRecord : redisHistory) {
            for (SysUserConversation pageRecord : pages.getRecords()) {
                if (redisRecord.getFriendId() != null && redisRecord.getFriendId().equals(pageRecord.getFriendId())) {
                    result.add(redisRecord);
                } else if (redisRecord.getGroupId() != null && redisRecord.getGroupId().equals(pageRecord.getGroupId())) {
                    result.add(redisRecord);
                } else {
                    result.add(pageRecord);
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




