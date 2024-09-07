package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUserFriend;
import top.th1nk.easychat.domain.vo.UserFriendVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_friend】的数据库操作Mapper
 * @createDate 2024-07-18 15:07:44
 * @Entity top.th1nk.easychat.domain.SysUserFriend
 */
public interface SysUserFriendMapper extends BaseMapper<SysUserFriend> {
    /**
     * 判断是否为双向好友
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否为好友
     */
    boolean isFriend(Integer userId, Integer friendId);

    /**
     * 判断 friendId 是否为 userId 的好友
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     */
    boolean isOneWayFriend(Integer userId, Integer friendId);

    /**
     * 获取好友信息Vo
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 好友信息Vo
     */
    UserFriendVo selectUserFriendVo(Integer userId, Integer friendId);

    /**
     * 根据用户ID和好友ID查询好友信息
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 好友信息
     */
    SysUserFriend selectByUserIdAndFriendId(Integer userId, Integer friendId);

    /**
     * 根据用户ID查询所有好友信息
     *
     * @param uid 用户ID
     * @return 好友信息列表
     */
    List<SysUserFriend> selectAllByUid(Integer uid);
}




