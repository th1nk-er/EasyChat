package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroupInvitation;
import top.th1nk.easychat.domain.vo.GroupInvitationVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_invitation】的数据库操作Service
 * @createDate 2024-08-27 22:20:11
 */
public interface SysGroupInvitationService extends IService<SysGroupInvitation> {
    /**
     * 刷新用户的邀请，将超过期限的邀请设置为过期状态
     *
     * @return 更新的记录数
     */
    int refreshAllInvitationStatus();

    /**
     * 获取用户群聊邀请列表
     *
     * @param page 页码
     * @return 邀请列表
     */
    List<GroupInvitationVo> getUserGroupInvitationList(int page);
}
