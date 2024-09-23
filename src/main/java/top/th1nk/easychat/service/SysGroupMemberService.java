package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.vo.GroupMemberVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_member】的数据库操作Service
 * @createDate 2024-08-27 22:20:11
 */
public interface SysGroupMemberService extends IService<SysGroupMember> {
    List<GroupMemberVo> getGroupMembers(int groupId, int pageNum);
}
