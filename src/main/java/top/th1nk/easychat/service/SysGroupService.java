package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.dto.CreateGroupDto;

/**
 * @author vinka
 * @description 针对表【ec_group】的数据库操作Service
 * @createDate 2024-08-27 22:20:11
 */
public interface SysGroupService extends IService<SysGroup> {
    /**
     * 创建群聊
     *
     * @param createGroupDto 创建群聊请求Dto
     * @return 是否创建成功
     */
    boolean createGroup(CreateGroupDto createGroupDto);
}
