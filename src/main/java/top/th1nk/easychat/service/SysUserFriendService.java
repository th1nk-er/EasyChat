package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nullable;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.dto.UserFriendUpdateDto;
import top.th1nk.easychat.domain.vo.FriendListVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;

/**
 * @author vinka
 * @description 针对表【ec_user_friend】的数据库操作Service
 * @createDate 2024-07-18 15:07:44
 */
public interface SysUserFriendService extends IService<SysUserFriend> {

    /**
     * 发送好友申请
     *
     * @param addFriendDto 好友申请信息
     * @return 是否发送成功
     */
    boolean sendAddRequest(AddFriendDto addFriendDto);

    /**
     * 处理好友申请
     *
     * @param friendRequestHandleDto 好友申请处理信息
     * @return 是否处理成功
     */
    boolean handleAddRequest(FriendRequestHandleDto friendRequestHandleDto);

    /**
     * 获取好友列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @return 好友列表
     */
    FriendListVo getFriendList(int userId, int page);

    /**
     * 获取好友信息
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 好友信息
     */
    @Nullable
    UserFriendVo getFriendInfo(int userId, int friendId);

    /**
     * 更新好友信息
     *
     * @param userId              用户ID
     * @param userFriendUpdateDto 更新信息
     * @return 是否更新成功
     */
    boolean updateFriendInfo(int userId, UserFriendUpdateDto userFriendUpdateDto);

    /**
     * 删除好友
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否删除成功
     */
    boolean deleteFriend(int userId, int friendId);
}
