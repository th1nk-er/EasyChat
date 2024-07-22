package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.dto.AddFriendDto;
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
import top.th1nk.easychat.service.SysUserFriendService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

/**
 * @author vinka
 * @description 针对表【ec_user_friend】的数据库操作Service实现
 * @createDate 2024-07-18 15:07:44
 */
@Service
public class SysUserFriendServiceImpl extends ServiceImpl<SysUserFriendMapper, SysUserFriend>
        implements SysUserFriendService {
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private SysUserAddFriendMapper sysUserAddFriendMapper;

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
}




