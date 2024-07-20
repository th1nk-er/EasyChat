package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.dto.AddFriendDto;

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
}
