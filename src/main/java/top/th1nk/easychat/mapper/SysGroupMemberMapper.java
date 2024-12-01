package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.vo.GroupMemberInfoVo;
import top.th1nk.easychat.domain.vo.GroupMemberVo;
import top.th1nk.easychat.enums.UserRole;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member】的数据库操作Mapper
 * @createDate 2024-08-27 22:20:11
 * @Entity top.th1nk.easychat.domain.SysGroupMember
 */
public interface SysGroupMemberMapper extends BaseMapper<SysGroupMember> {
    /**
     * 获取用户为指定身份的群组数量
     *
     * @param userId 用户ID
     * @return 群组数量
     */
    int countGroupsByUserRole(Integer userId, UserRole userRole);

    /**
     * 根据用户ID和群组ID查询群组成员信息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 群组成员信息
     */
    SysGroupMember selectByUserIdAndGroupId(Integer userId, Integer groupId);

    /**
     * 根据用户ID查询其在所有群的群成员信息
     *
     * @param userId 用户ID
     * @return 群组成员信息列表
     */
    List<SysGroupMember> selectAllByUserId(Integer userId);

    /**
     * 根据群组ID查询所有群组成员信息
     *
     * @param page    分页对象
     * @param groupId 群组ID
     * @return 群组成员信息列表
     */
    List<GroupMemberVo> selectGroupMemberVo(IPage<?> page, int groupId);

    /**
     * 根据用户ID和群组ID查询群组成员信息
     *
     * @param userId  用户ID
     * @param groupId 群组ID
     * @return 群组成员信息
     */
    GroupMemberInfoVo selectGroupMemberInfoVo(Integer userId, Integer groupId);

    /**
     * 分页查询群组成员详细信息
     *
     * @param page    分页对象
     * @param groupId 群组ID
     * @return 群组成员详细信息列表
     */
    List<GroupMemberInfoVo> selectGroupMemberInfoVoList(IPage<?> page, Integer groupId);

    /**
     * 查询群组的群主
     *
     * @param groupId 群组ID
     * @return 群主信息
     */
    SysGroupMember selectGroupLeader(int groupId);
}




