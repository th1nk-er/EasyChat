package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import top.th1nk.easychat.domain.SysGroupNotification;
import top.th1nk.easychat.domain.vo.GroupNotificationVo;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group_notification】的数据库操作Mapper
 * @createDate 2024-08-27 22:20:11
 * @Entity top.th1nk.easychat.domain.SysGroupInvitation
 */
public interface SysGroupNotificationMapper extends BaseMapper<SysGroupNotification> {
    /**
     * 获取用户的群聊通知列表
     *
     * @param page   分页
     * @param userId 用户ID
     * @return 群聊通知列表
     */
    List<GroupNotificationVo> selectNotificationVoByUserId(IPage<?> page, int userId);
}




