package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.vo.FriendListVo;

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
     * @param page 页码
     * @return 好友列表
     */
    FriendListVo getFriendList(int page);
}
