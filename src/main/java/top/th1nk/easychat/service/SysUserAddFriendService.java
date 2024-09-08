package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import top.th1nk.easychat.domain.SysUserAddFriend;
import top.th1nk.easychat.domain.vo.FriendRequestListVo;

/**
 * @author vinka
 * @description 针对表【ec_user_add_friend】的数据库操作Service
 * @createDate 2024-07-18 15:07:53
 */
public interface SysUserAddFriendService extends IService<SysUserAddFriend> {

    /**
     * 获取好友申请列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @return 好友申请列表Vo
     */
    @NotNull
    FriendRequestListVo getFriendRequestList(int userId, int page);

    /**
     * 通过一方的好友申请ID返回另一方的好友申请
     *
     * @param id 好友申请ID
     * @return 好友申请
     */
    @Nullable
    SysUserAddFriend getStrangerRequestById(int id);

    /**
     * 获取待处理的好友申请
     * 返回用户ID向陌生人ID发起的待处理的请求
     *
     * @param userId     用户ID
     * @param strangerId 陌生人ID
     * @return 待处理的好友申请
     */
    @Nullable
    SysUserAddFriend getPendingRequest(int userId, int strangerId);

    /**
     * 判断好友申请是否过期
     *
     * @param sysUserAddFriend 好友申请
     * @return 是否过期
     */
    boolean isRequestExpired(SysUserAddFriend sysUserAddFriend);
}
