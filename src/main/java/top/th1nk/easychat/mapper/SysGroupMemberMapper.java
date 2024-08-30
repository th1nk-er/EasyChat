package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.enums.UserRole;

/**
 * @author vinka
 * @description 针对表【ec_group_member】的数据库操作Mapper
 * @createDate 2024-08-27 22:20:11
 * @Entity top.th1nk.easychat.domain.SysGroupMember
 */
public interface SysGroupMemberMapper extends BaseMapper<SysGroupMember> {
    /**
     * 获取用户为指定身份的群组数量
     * @param userId 用户ID
     * @return 群组数量
     */
    int countGroupsByUserRole(Integer userId, UserRole userRole);
}




