package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.vo.FriendRequestListVo;
import top.th1nk.easychat.enums.AddUserStatus;
import top.th1nk.easychat.enums.AddUserType;
import top.th1nk.easychat.mapper.SysUserAddFriendMapper;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.SysUserAddFriendService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_add_friend】的数据库操作Service实现
 * @createDate 2024-07-18 15:07:53
 */
@Service
public class SysUserAddFriendServiceImpl extends ServiceImpl<SysUserAddFriendMapper, SysUserAddFriend>
        implements SysUserAddFriendService {
    private static final long REQUEST_EXPIRE_TIME = 60 * 60 * 24 * 7; // 好友申请有效期：7天 单位：秒
    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public FriendRequestListVo getFriendRequestList(int userId, int page) {
        FriendRequestListVo friendRequestListVo = new FriendRequestListVo();
        friendRequestListVo.setTotal(0);
        friendRequestListVo.setPageSize(10);
        friendRequestListVo.setRecords(List.of());
        LambdaQueryWrapper<SysUserAddFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserAddFriend::getUid, userId);
        Page<SysUserAddFriend> selectedPage = baseMapper.selectPage(new Page<>(page, 10), qw);
        List<SysUserAddFriend> userAddFriendList = selectedPage.getRecords();
        // 按添加时间排序
        userAddFriendList.sort((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime()));
        if (userAddFriendList.isEmpty()) return friendRequestListVo;
        friendRequestListVo.setTotal(selectedPage.getTotal());
        // 获取陌生人用户ID
        List<Integer> strangerIds = userAddFriendList.stream().map(SysUserAddFriend::getStrangerId).toList();
        // 查询对应的用户信息
        List<SysUser> sysUsers = strangerIds
                .stream()
                .map(id -> sysUserMapper.selectById(id))
                .toList();
        // 将用户信息封装到vo中
        List<FriendRequestListVo.Record> records = new ArrayList<>();
        for (int i = 0; i < userAddFriendList.size(); i++) {
            FriendRequestListVo.Record record = new FriendRequestListVo.Record();
            BeanUtils.copyProperties(userAddFriendList.get(i), record);
            record.setSex(sysUsers.get(i).getSex());
            record.setAvatar(sysUsers.get(i).getAvatar());
            record.setNickname(sysUsers.get(i).getNickname());
            record.setUsername(sysUsers.get(i).getUsername());
            records.add(record);
        }
        friendRequestListVo.setRecords(records);
        return friendRequestListVo;
    }

    @Nullable
    @Override
    public SysUserAddFriend getStrangerRequestById(int id) {
        SysUserAddFriend friendRequest = baseMapper.selectById(id);
        if (friendRequest == null) return null;
        LambdaQueryWrapper<SysUserAddFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserAddFriend::getStrangerId, friendRequest.getUid());
        qw.eq(SysUserAddFriend::getUid, friendRequest.getStrangerId());
        if (friendRequest.getStatus() == AddUserStatus.IGNORED)
            qw.eq(SysUserAddFriend::getStatus, AddUserStatus.PENDING);
        else qw.eq(SysUserAddFriend::getStatus, friendRequest.getStatus());
        qw.eq(SysUserAddFriend::getAddType, AddUserType.ADD_OTHER);
        qw.eq(SysUserAddFriend::getAddInfo, friendRequest.getAddInfo());
        qw.between(SysUserAddFriend::getCreateTime, friendRequest.getCreateTime().minusSeconds(1), friendRequest.getCreateTime().plusSeconds(1));
        return baseMapper.selectOne(qw);
    }

    @Nullable
    @Override
    public SysUserAddFriend getPendingRequest(int userId, int strangerId) {
        if (!baseMapper.isAddRequestPending(userId, strangerId))
            return null;
        LambdaQueryWrapper<SysUserAddFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserAddFriend::getUid, userId)
                .eq(SysUserAddFriend::getStrangerId, strangerId)
                .eq(SysUserAddFriend::getStatus, AddUserStatus.PENDING)
                .eq(SysUserAddFriend::getAddType, AddUserType.ADD_OTHER);
        List<SysUserAddFriend> requestList = baseMapper.selectList(qw);
        if (requestList.isEmpty())
            return null;
        for (SysUserAddFriend request : requestList) {
            if (!isRequestExpired(request))
                return request;
        }
        return null;
    }

    @Override
    public boolean isRequestExpired(SysUserAddFriend sysUserAddFriend) {
        return sysUserAddFriend.getCreateTime().plusSeconds(REQUEST_EXPIRE_TIME).isBefore(LocalDateTime.now());
    }
}




