package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.vo.UserGroupVo;

/**
 * @author vinka
 * @description 针对表【ec_group】的数据库操作Mapper
 * @createDate 2024-08-27 22:20:11
 * @Entity top.th1nk.easychat.domain.SysGroup
 */
public interface SysGroupMapper extends BaseMapper<SysGroup> {
    IPage<UserGroupVo> selectUserGroupList(IPage<?> page, int userId);
}




