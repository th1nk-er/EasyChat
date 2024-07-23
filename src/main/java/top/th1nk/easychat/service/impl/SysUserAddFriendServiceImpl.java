package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.mapper.SysUserAddFriendMapper;
import top.th1nk.easychat.service.SysUserAddFriendService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_add_friend】的数据库操作Service实现
 * @createDate 2024-07-18 15:07:53
 */
@Service
public class SysUserAddFriendServiceImpl extends ServiceImpl<SysUserAddFriendMapper, SysUserAddFriend>
        implements SysUserAddFriendService {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public List<SysUserAddFriend> getFriendRequestList(int page) {
        String tokenString = RequestUtils.getUserTokenString();
        if (tokenString.isEmpty()) return List.of();
        UserVo userVo = jwtUtils.parseToken(tokenString);
        if (userVo == null || userVo.getId() == null)
            return List.of();
        // 开始查询
        LambdaQueryWrapper<SysUserAddFriend> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserAddFriend::getUid, userVo.getId());
        Page<SysUserAddFriend> selectedPage = baseMapper.selectPage(new Page<>(page, 10), qw);
        return selectedPage.getRecords();
    }
}




