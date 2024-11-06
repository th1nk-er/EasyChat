package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupMemberMuted;

/**
 * @author vinka
 * @description 针对表【ec_group_member_muted】的数据库操作Service
 * @createDate 2024-11-04 14:06:48
 */
public interface SysGroupMemberMutedService extends IService<SysGroupMemberMuted> {
    /**
     * 禁言群成员
     *
     * @param groupId  群聊ID
     * @param memberId 群成员ID
     * @param adminId  执行禁言的管理员ID
     * @param duration 禁言时长，单位：分钟
     * @return 是否成功
     */
    boolean muteMember(int groupId, int memberId, int adminId, int duration);

    /**
     * 解除禁言群成员
     *
     * @param groupId  群聊ID
     * @param memberId 群成员ID
     * @param adminId  执行解除禁言的管理员ID
     * @return 是否成功
     */
    boolean unmuteMember(int groupId, int memberId, int adminId);
}
