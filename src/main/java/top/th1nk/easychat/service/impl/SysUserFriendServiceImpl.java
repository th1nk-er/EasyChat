package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.AddUserStatus;
import top.th1nk.easychat.enums.AddUserType;
import top.th1nk.easychat.enums.CommonExceptionEnum;
import top.th1nk.easychat.enums.UserFriendExceptionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.UserFriendException;
import top.th1nk.easychat.mapper.SysUserAddFriendMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.SysUserAddFriendService;
import top.th1nk.easychat.service.SysUserFriendService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.time.LocalDateTime;

/**
 * @author vinka
 * @description 针对表【ec_user_friend】的数据库操作Service实现
 * @createDate 2024-07-18 15:07:44
 */
@Service
public class SysUserFriendServiceImpl extends ServiceImpl<SysUserFriendMapper, SysUserFriend>
        implements SysUserFriendService {
    private static final long REQUEST_EXPIRE_TIME = 60 * 60 * 24 * 7; // 好友申请有效期：7天 单位：秒
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserAddFriendMapper sysUserAddFriendMapper;
    @Resource
    private SysUserAddFriendService sysUserAddFriendService;

    @Resource
    private JwtUtils jwtUtils;

    @Transactional
    @Override
    public boolean sendAddRequest(AddFriendDto addFriendDto) {
        if (addFriendDto == null) return false;
        String userTokenString = RequestUtils.getUserTokenString();
        UserVo userVo = jwtUtils.parseToken(userTokenString);
        if (userVo == null) return false;
        if (userVo.getId() == null) return false;
        if (userVo.getId().equals(addFriendDto.getAddId()))
            throw new UserFriendException(UserFriendExceptionEnum.CANNOT_ADD_SELF); // 禁止添加自己
        if (sysUserMapper.selectById(addFriendDto.getAddId()) == null)
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (baseMapper.isFriend(userVo.getId(), addFriendDto.getAddId()))
            throw new UserFriendException(UserFriendExceptionEnum.ALREADY_FRIEND);
        // 判断stranger是否已有未处理的申请
        if (sysUserAddFriendMapper.isAddRequestPending(addFriendDto.getAddId(), userVo.getId()))
            throw new UserFriendException(UserFriendExceptionEnum.ADD_REQUEST_EXIST);
        // 发送好友申请
        SysUserAddFriend addOther = new SysUserAddFriend();
        SysUserAddFriend addByOther = new SysUserAddFriend();
        addOther.setUid(userVo.getId());
        addOther.setAddType(AddUserType.ADD_OTHER);
        addOther.setStatus(AddUserStatus.PENDING);
        addOther.setAddInfo(addFriendDto.getAddInfo());
        addOther.setStrangerId(addFriendDto.getAddId());

        BeanUtils.copyProperties(addOther, addByOther);
        sysUserAddFriendMapper.insert(addOther);

        // 对方被添加
        addByOther.setAddType(AddUserType.ADD_BY_OTHER);
        addByOther.setUid(addFriendDto.getAddId());
        addByOther.setStrangerId(userVo.getId());
        sysUserAddFriendMapper.insert(addByOther);
        return true;
    }

    @Transactional
    @Override
    public boolean handleAddRequest(FriendRequestHandleDto friendRequestHandleDto) {
        if (friendRequestHandleDto == null || friendRequestHandleDto.getStatus() == null)
            return false;
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null) return false;
        SysUserAddFriend friendRequest = sysUserAddFriendMapper.selectById(friendRequestHandleDto.getId());
        if (friendRequest == null) return false;
        if (friendRequest.getUid().equals(friendRequest.getStrangerId()))
            throw new UserFriendException(UserFriendExceptionEnum.CANNOT_ADD_SELF); // 禁止添加自己
        if (!friendRequest.getUid().equals(userVo.getId()))
            return false; // 防止恶意操作，只有自己发送的请求才能处理
        if (friendRequest.getAddType() == AddUserType.ADD_OTHER)
            return false; // 防止恶意操作，只有被他人添加的请求才能处理
        if (baseMapper.isFriend(userVo.getId(), friendRequest.getId()))
            throw new UserFriendException(UserFriendExceptionEnum.ALREADY_FRIEND);
        if (friendRequest.getStatus() != AddUserStatus.PENDING || friendRequest.getCreateTime().plusSeconds(REQUEST_EXPIRE_TIME).isBefore(LocalDateTime.now()))
            throw new UserFriendException(UserFriendExceptionEnum.ADD_REQUEST_EXPIRED);
        SysUserAddFriend other = sysUserAddFriendService.getStrangerRequestById(friendRequestHandleDto.getId());
        if (friendRequestHandleDto.getStatus() == AddUserStatus.AGREED) {
            // 同意申请
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
            baseMapper.insert(userFriend);
            userFriend = new SysUserFriend();
            userFriend.setUid(friendRequest.getStrangerId());
            userFriend.setFriendId(friendRequest.getUid());
            baseMapper.insert(userFriend);
            return true;
        } else if (friendRequestHandleDto.getStatus() == AddUserStatus.REFUSED) {
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
            // 忽略申请
            // 仅将一方的状态设置为忽略
            friendRequest.setStatus(AddUserStatus.IGNORED);
            return sysUserAddFriendMapper.updateById(friendRequest) == 1;
        }
        return false;
    }
}




