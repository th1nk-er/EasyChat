package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupMemberIgnored;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member_ignore】的数据库操作Service
 * @createDate 2024-10-20 18:46:12
 */
public interface SysGroupMemberIgnoredService extends IService<SysGroupMemberIgnored> {
    /**
     * 屏蔽群组成员
     *
     * @param userId    用户ID
     * @param groupId   群组ID
     * @param ignoredId 被屏蔽的成员ID
     * @return 是否成功
     */
    boolean ignoreMemberForUser(int userId, int groupId, int ignoredId);

    /**
     * 取消屏蔽群组成员
     *
     * @param userId    用户ID
     * @param groupId   群组ID
     * @param ignoredId 被屏蔽的成员ID
     * @return 是否成功
     */
    boolean cancelIgnoreMemberForUser(int userId, int groupId, int ignoredId);

    /**
     * 获取用户屏蔽的群组成员ID列表
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 屏蔽的成员ID列表
     */
    List<Integer> getIgnoredMemberIds(int userId, int groupId);
}
