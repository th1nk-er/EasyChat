package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.vo.GroupMemberInfoVo;
import top.th1nk.easychat.domain.vo.GroupMemberVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member】的数据库操作Service
 * @createDate 2024-08-27 22:20:11
 */
public interface SysGroupMemberService extends IService<SysGroupMember> {
    /**
     * 分页获取群组成员信息
     *
     * @param groupId 群组ID
     * @param pageNum 当前页码
     * @return 群组成员信息列表
     */
    List<GroupMemberVo> getGroupMembers(int groupId, int pageNum);

    /**
     * 获取群组成员的详细信息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 群组成员信息
     */
    GroupMemberInfoVo getGroupMemberInfo(int userId, int groupId);

    /**
     * 退出群组
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 是否成功
     */
    boolean quitGroup(int userId, int groupId);
}
