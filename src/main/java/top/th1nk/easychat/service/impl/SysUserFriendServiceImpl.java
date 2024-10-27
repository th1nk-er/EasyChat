package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.dto.UserFriendUpdateDto;
import top.th1nk.easychat.domain.vo.FriendListVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;
import top.th1nk.easychat.enums.AddUserStatus;
import top.th1nk.easychat.enums.AddUserType;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.UserFriendException;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.enums.UserFriendExceptionEnum;
import top.th1nk.easychat.mapper.SysUserAddFriendMapper;
import top.th1nk.easychat.mapper.SysUserConversationMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.ConversationRedisService;
import top.th1nk.easychat.service.SysUserAddFriendService;
import top.th1nk.easychat.service.SysUserFriendService;
import top.th1nk.easychat.utils.SecurityUtils;
import top.th1nk.easychat.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_friend】的数据库操作Service实现
 * @createDate 2024-07-18 15:07:44
 */
@Slf4j
@Service
public class SysUserFriendServiceImpl extends ServiceImpl<SysUserFriendMapper, SysUserFriend>
        implements SysUserFriendService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserAddFriendMapper sysUserAddFriendMapper;
    @Resource
    private SysUserAddFriendService sysUserAddFriendService;
    @Resource
    private SysUserConversationMapper sysUserConversationMapper;
    @Resource
    private ConversationRedisService conversationRedisService;
    @Resource
    private SecurityUtils securityUtils;

    @Transactional
    @Override
    public boolean sendAddRequest(AddFriendDto addFriendDto) {
        if (addFriendDto == null) return false;
        log.debug("用户发起添加好友请求 userId: {} addId: {}", addFriendDto.getUserId(), addFriendDto.getAddId());
        if (addFriendDto.getUserId() == addFriendDto.getAddId())
            throw new UserFriendException(UserFriendExceptionEnum.CANNOT_ADD_SELF); // 禁止添加自己
        if (sysUserMapper.selectById(addFriendDto.getAddId()) == null)
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (baseMapper.isOneWayFriend(addFriendDto.getUserId(), addFriendDto.getAddId()))
            throw new UserFriendException(UserFriendExceptionEnum.ALREADY_FRIEND);
        // 判断是否已有未处理的申请
        if (sysUserAddFriendService.getPendingRequest(addFriendDto.getUserId(), addFriendDto.getAddId()) != null)
            throw new UserFriendException(UserFriendExceptionEnum.ADD_REQUEST_EXIST);
        log.debug("用户添加好友 userId: {} addId: {}", addFriendDto.getUserId(), addFriendDto.getAddId());
        // 发送好友申请
        SysUserAddFriend addOther = new SysUserAddFriend();
        SysUserAddFriend addByOther = new SysUserAddFriend();
        addOther.setUid(addFriendDto.getUserId());
        addOther.setAddType(AddUserType.ADD_OTHER);
        addOther.setStatus(AddUserStatus.PENDING);
        addOther.setAddInfo(addFriendDto.getAddInfo());
        addOther.setStrangerId(addFriendDto.getAddId());

        BeanUtils.copyProperties(addOther, addByOther);
        sysUserAddFriendMapper.insert(addOther);

        // 对方被添加
        addByOther.setAddType(AddUserType.ADD_BY_OTHER);
        addByOther.setUid(addFriendDto.getAddId());
        addByOther.setStrangerId(addFriendDto.getUserId());
        sysUserAddFriendMapper.insert(addByOther);
        return true;
    }

    @Transactional
    @Override
    public boolean handleAddRequest(FriendRequestHandleDto friendRequestHandleDto) {
        if (friendRequestHandleDto == null || friendRequestHandleDto.getStatus() == null)
            return false;
        SysUserAddFriend friendRequest = sysUserAddFriendMapper.selectById(friendRequestHandleDto.getId());
        if (friendRequest == null) return false;
        if (friendRequest.getUid().equals(friendRequest.getStrangerId()))
            throw new UserFriendException(UserFriendExceptionEnum.CANNOT_ADD_SELF); // 禁止添加自己
        if (!friendRequest.getUid().equals(friendRequestHandleDto.getUserId()))
            return false; // 防止恶意操作，只有自己发送的请求才能处理
        if (friendRequest.getAddType() == AddUserType.ADD_OTHER)
            return false; // 防止恶意操作，只有被他人添加的请求才能处理
        if (baseMapper.isFriend(friendRequestHandleDto.getUserId(), friendRequest.getId()))
            throw new UserFriendException(UserFriendExceptionEnum.ALREADY_FRIEND);
        if (friendRequest.getStatus() != AddUserStatus.PENDING || sysUserAddFriendService.isRequestExpired(friendRequest))
            throw new UserFriendException(UserFriendExceptionEnum.ADD_REQUEST_EXPIRED);
        SysUserAddFriend other = sysUserAddFriendService.getStrangerRequestById(friendRequestHandleDto.getId());
        if (friendRequestHandleDto.getStatus() == AddUserStatus.AGREED) {
            log.debug("用户同意好友申请 userId: {} strangerId: {}", friendRequest.getUid(), friendRequest.getStrangerId());
            // 同意申请
            securityUtils.clearPermissionsCache(friendRequest.getUid());
            securityUtils.clearPermissionsCache(friendRequest.getStrangerId());
            friendRequest.setStatus(AddUserStatus.AGREED);
            if (sysUserAddFriendMapper.updateById(friendRequest) != 1)
                throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
            // 将对方的状态也设置为已同意
            if (other == null) return false;
            other.setStatus(AddUserStatus.AGREED);
            if (sysUserAddFriendMapper.updateById(other) != 1) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return false;
            }
            SysUserFriend userFriend = new SysUserFriend();
            userFriend.setUid(friendRequest.getUid());
            userFriend.setFriendId(friendRequest.getStrangerId());
            userFriend.setMuted(false);
            if (!baseMapper.isOneWayFriend(friendRequestHandleDto.getUserId(), friendRequest.getStrangerId())) // 当没有好友关系时才保存数据
                baseMapper.insert(userFriend);
            userFriend = new SysUserFriend();
            userFriend.setUid(friendRequest.getStrangerId());
            userFriend.setFriendId(friendRequest.getUid());
            userFriend.setMuted(false);
            baseMapper.insert(userFriend);
            return true;
        } else if (friendRequestHandleDto.getStatus() == AddUserStatus.REFUSED) {
            log.debug("用户拒绝好友申请 userId: {} strangerId: {}", friendRequest.getUid(), friendRequest.getStrangerId());
            // 拒绝申请
            friendRequest.setStatus(AddUserStatus.REFUSED);
            friendRequest.setAddInfo(friendRequest.getAddInfo() + "    拒绝理由：" + friendRequestHandleDto.getRemark());
            if (sysUserAddFriendMapper.updateById(friendRequest) != 1)
                throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
            // 将对方的状态也设置为已拒绝
            if (other == null)
                throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
            other.setStatus(AddUserStatus.REFUSED);
            other.setAddInfo(friendRequest.getAddInfo());
            if (sysUserAddFriendMapper.updateById(other) != 1)
                throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
            return true;
        } else if (friendRequestHandleDto.getStatus() == AddUserStatus.IGNORED) {
            log.debug("用户忽略好友申请 userId: {} strangerId: {}", friendRequest.getUid(), friendRequest.getStrangerId());
            // 忽略申请
            // 仅将一方的状态设置为忽略
            friendRequest.setStatus(AddUserStatus.IGNORED);
            return sysUserAddFriendMapper.updateById(friendRequest) == 1;
        }
        return false;
    }

    @Override
    public FriendListVo getFriendList(int userId, int page) {
        log.debug("获取用户好友列表 userId: {}", userId);
        FriendListVo friendListVo = new FriendListVo();
        friendListVo.setTotal(0);
        friendListVo.setPageSize(10);
        friendListVo.setRecords(List.of());
        LambdaQueryWrapper<SysUserFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserFriend::getUid, userId);
        Page<SysUserFriend> ipage = baseMapper.selectPage(new Page<>(page, friendListVo.getPageSize()), qw);
        friendListVo.setTotal(ipage.getTotal());
        List<UserFriendVo> records = new ArrayList<>();
        ipage.getRecords().forEach(userFriend -> records.add(baseMapper.selectUserFriendVo(userId, userFriend.getFriendId())));
        friendListVo.setRecords(records);
        return friendListVo;
    }

    @Override
    public UserFriendVo getFriendInfo(int userId, int friendId) {
        log.debug("获取用户好友详细信息 userId: {} friendId: {}", userId, friendId);
        return baseMapper.selectUserFriendVo(userId, friendId);
    }

    @Override
    public boolean updateFriendInfo(int userId, UserFriendUpdateDto userFriendUpdateDto) {
        if (!baseMapper.isOneWayFriend(userId, userFriendUpdateDto.getFriendId()))
            throw new UserFriendException(UserFriendExceptionEnum.NOT_FRIEND);
        SysUserFriend sysUserFriend = baseMapper.selectByUserIdAndFriendId(userId, userFriendUpdateDto.getFriendId());
        log.debug("更新用户好友信息 userId: {} friendId: {}", userId, userFriendUpdateDto.getFriendId());
        if (userFriendUpdateDto.getRemark() != null) {
            if (UserUtils.isValidRemark(userFriendUpdateDto.getRemark()))
                sysUserFriend.setRemark(userFriendUpdateDto.getRemark());
            else throw new UserFriendException(UserFriendExceptionEnum.INVALID_REMARK);
            userFriendUpdateDto.setRemark(userFriendUpdateDto.getRemark().trim());
        }
        sysUserFriend.setMuted(userFriendUpdateDto.isMuted());
        return baseMapper.updateById(sysUserFriend) == 1;
    }

    @Override
    @CacheEvict(cacheNames = "user:perms", key = "#userId", condition = "#result==true")
    public boolean deleteFriend(int userId, int friendId) {
        if (!baseMapper.isOneWayFriend(userId, friendId))
            throw new UserFriendException(UserFriendExceptionEnum.NOT_FRIEND);
        log.debug("用户删除好友 userId: {} friendId: {}", userId, friendId);
        // 删除对话列表中对应条目
        sysUserConversationMapper.deleteConversation(userId, friendId, ChatType.FRIEND);
        conversationRedisService.deleteConversation(userId, friendId, ChatType.FRIEND);
        // 删除好友
        LambdaQueryWrapper<SysUserFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserFriend::getUid, userId)
                .eq(SysUserFriend::getFriendId, friendId);
        return baseMapper.delete(qw) == 1;
    }
}




