package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.vo.GroupVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;

/**
 * @author vinka
 * @description 针对表【ec_group】的数据库操作Mapper
 * @createDate 2024-08-27 22:20:11
 * @Entity top.th1nk.easychat.domain.SysGroup
 */
public interface SysGroupMapper extends BaseMapper<SysGroup> {
    /**
     * 分页查询用户加入的群组
     *
     * @param page   分页
     * @param userId 用户ID
     * @return 分页数据
     */
    IPage<UserGroupVo> selectUserGroupList(IPage<?> page, int userId);

    /**
     * 根据群组ID查询群组信息
     *
     * @param groupId 群组ID
     * @return 群组信息
     */
    GroupVo selectGroupVoById(Integer groupId);

    /**
     * 查询用户群组信息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 用户群组信息
     */
    UserGroupVo selectUserGroupVo(int userId, int groupId);


    /**
     * 更新头像
     *
     * @param groupId 群聊ID
     * @param avatar  头像地址
     */
    int updateAvatar(int groupId, String avatar);

    /**
     * 查询相同头像的群聊数量
     *
     * @param avatar 头像
     * @return 群聊数量
     */
    int getSameAvatarCount(String avatar);
}




