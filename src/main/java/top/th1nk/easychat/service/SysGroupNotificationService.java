package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupNotification;
import top.th1nk.easychat.domain.vo.GroupNotificationVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_invitation】的数据库操作Service
 * @createDate 2024-08-27 22:20:11
 */
public interface SysGroupNotificationService extends IService<SysGroupNotification> {
    /**
     * 刷新用户的邀请，将超过期限的邀请设置为过期状态
     *
     * @return 更新的记录数
     */
    int refreshAllInvitationStatus();

    /**
     * 获取用户群聊通知列表
     *
     * @param userId 用户ID
     * @param page   页码
     * @return 通知列表
     */
    List<GroupNotificationVo> getUserGroupNotificationList(int userId, int page);

    /**
     * 用户接受群聊邀请
     *
     * @param userId  用户ID
     * @param groupId 群ID
     * @return 是否成功
     */
    boolean userAcceptInvitation(int userId, int groupId);

    /**
     * 用户拒绝群聊邀请
     *
     * @param userId  用户ID
     * @param groupId 群ID
     * @return 是否成功
     */
    boolean userRejectInvitation(int userId, int groupId);

    /**
     * 管理员同意用户的进群邀请
     *
     * @param userId  用户ID
     * @param groupId 群ID
     * @return 是否成功
     */
    boolean adminAcceptInvitation(int userId, int groupId);

    /**
     * 管理员拒绝用户的进群邀请
     *
     * @param userId  用户ID
     * @param groupId 群ID
     * @return 是否成功
     */
    boolean adminRejectInvitation(int userId, int groupId);

    /**
     * 邀请用户加入群聊
     *
     * @param userId    用户ID
     * @param groupId   群ID
     * @param memberIds 被邀请的用户ID列表
     * @return 是否成功
     */
    boolean inviteMembers(int userId, int groupId, List<Integer> memberIds);
}
