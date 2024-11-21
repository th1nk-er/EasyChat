package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.annotation.Nullable;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.domain.dto.GroupUpdateDto;
import top.th1nk.easychat.domain.dto.UserGroupUpdateDto;
import top.th1nk.easychat.domain.vo.GroupVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;

import java.util.List;

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

    /**
     * 获取用户群聊列表
     *
     * @param userId  用户ID
     * @param pageNum 页码
     * @return 群聊列表
     */
    List<UserGroupVo> getUserGroupList(int userId, int pageNum);

    /**
     * 获取群聊信息Vo
     *
     * @param groupId 群聊ID
     * @return 群聊信息
     */
    GroupVo getGroupVo(int groupId);

    /**
     * 更新用户群组信息
     *
     * @param userId             用户ID
     * @param userGroupUpdateDto 用户群组信息更新Dto
     * @return 是否更新成功
     */
    boolean updateUserGroupInfo(int userId, UserGroupUpdateDto userGroupUpdateDto);

    /**
     * 更新群聊头像
     *
     * @param userId  管理员ID
     * @param groupId 群聊ID
     * @param file    头像文件
     * @return 上传后的头像路径, null 表示上传失败
     */
    @Nullable
    String updateAvatar(int userId, int groupId, MultipartFile file);

    /**
     * 更新群聊信息
     *
     * @param groupId        群聊ID
     * @param groupUpdateDto 群聊信息更新Dto
     * @return 是否更新成功
     */
    boolean updateGroupInfo(int groupId, GroupUpdateDto groupUpdateDto);
}
