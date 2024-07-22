package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUserAddFriend;

/**
 * @author vinka
 * @description 针对表【ec_user_add_friend】的数据库操作Mapper
 * @createDate 2024-07-18 15:07:53
 * @Entity top.th1nk.easychat.domain.SysUserAddFriend
 */
public interface SysUserAddFriendMapper extends BaseMapper<SysUserAddFriend> {

    /**
     * 判断用户对陌生人的好友申请是否未处理
     *
     * @param uid        用户ID
     * @param strangerId 陌生人ID
     * @return 未处理返回-true 已处理或申请不存在返回-false
     */
    boolean isAddRequestPending(int uid, int strangerId);
}




