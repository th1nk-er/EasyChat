package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.config.easychat.GroupProperties;
import top.th1nk.easychat.domain.SysGroup;
import top.th1nk.easychat.domain.SysGroupMember;
import top.th1nk.easychat.domain.SysGroupNotification;
import top.th1nk.easychat.domain.chat.ChatType;
import top.th1nk.easychat.domain.chat.MessageCommand;
import top.th1nk.easychat.domain.chat.WSMessage;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.domain.dto.GroupUpdateDto;
import top.th1nk.easychat.domain.dto.UserGroupUpdateDto;
import top.th1nk.easychat.domain.vo.GroupVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.GroupNotificationType;
import top.th1nk.easychat.enums.GroupStatus;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.GroupException;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.enums.GroupExceptionEnum;
import top.th1nk.easychat.mapper.SysGroupMapper;
import top.th1nk.easychat.mapper.SysGroupMemberMapper;
import top.th1nk.easychat.mapper.SysGroupNotificationMapper;
import top.th1nk.easychat.mapper.SysUserFriendMapper;
import top.th1nk.easychat.service.MinioService;
import top.th1nk.easychat.service.SysGroupService;
import top.th1nk.easychat.service.SysUserConversationService;
import top.th1nk.easychat.service.WebSocketService;
import top.th1nk.easychat.utils.FileUtils;
import top.th1nk.easychat.utils.GroupUtils;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_group】的数据库操作Service实现
 * @createDate 2024-08-27 22:20:11
 */
@Slf4j
@Service
public class SysGroupServiceImpl extends ServiceImpl<SysGroupMapper, SysGroup>
        implements SysGroupService {
    @Resource
    private GroupProperties groupProperties;
    @Resource
    private SysGroupMemberMapper sysGroupMemberMapper;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SysUserFriendMapper sysUserFriendMapper;
    @Resource
    private SysGroupNotificationMapper sysGroupNotificationMapper;
    @Resource
    private SysUserConversationService sysUserConversationService;
    @Resource
    private MinioService minioService;
    @Resource
    private WebSocketService webSocketService;

    @Transactional
    @Override
    @CacheEvict(cacheNames = "user:perms", key = "#createGroupDto.getUserId()", condition = "#result==true")
    public boolean createGroup(CreateGroupDto createGroupDto) {
        if (!GroupUtils.isValidGroupName(createGroupDto.getGroupName())) {
            throw new GroupException(GroupExceptionEnum.INVALID_GROUP_NAME);
        }
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getId() == null) return false;
        List<Integer> friendIds = new ArrayList<>();
        createGroupDto.getFriendIds().forEach(id -> {
            if (id != null && sysUserFriendMapper.isOneWayFriend(id, userVo.getId()))
                friendIds.add(id);
        });
        if (friendIds.isEmpty()) {
            log.debug("群聊邀请人数为空");
            return false;
        }
        if (sysGroupMemberMapper.countGroupsByUserRole(userVo.getId(), UserRole.LEADER) >= groupProperties.getMaxGroupPerUser()) {
            throw new GroupException(GroupExceptionEnum.CREATABLE_GROUP_LIMIT_EXCEEDED);
        }
        log.debug("创建群聊,用户ID:{},群聊名称:{},邀请人数:{}", userVo.getId(), createGroupDto.getGroupName(), friendIds.size());
        // 创建群聊
        SysGroup group = new SysGroup();
        group.setGroupName(createGroupDto.getGroupName());
        group.setStatus(GroupStatus.NORMAL);
        group.setAvatar("/" + groupProperties.getAvatarDir() + "/" + groupProperties.getDefaultAvatarName());
        baseMapper.insert(group);
        // 设置群主
        SysGroupMember sysGroupMember = new SysGroupMember();
        sysGroupMember.setGroupId(group.getGroupId());
        sysGroupMember.setUserId(userVo.getId());
        sysGroupMember.setRole(UserRole.LEADER);
        sysGroupMember.setMuted(false);
        sysGroupMemberMapper.insert(sysGroupMember);
        // 发送邀请
        friendIds.forEach(id -> {
            SysGroupNotification invitation = new SysGroupNotification();
            invitation.setGroupId(group.getGroupId());
            invitation.setOperatorId(userVo.getId());
            invitation.setTargetId(id);
            invitation.setType(GroupNotificationType.PENDING);
            sysGroupNotificationMapper.insert(invitation);
        });
        // 将群聊加入到用户会话中
        sysUserConversationService.setConversationRead(userVo.getId(), group.getGroupId(), ChatType.GROUP);
        return true;
    }

    @Override
    @NotNull
    public List<UserGroupVo> getUserGroupList(int userId, int pageNum) {
        if (pageNum <= 0) return List.of();
        log.debug("获取用户群聊列表,userId:{},pageNum:{}", userId, pageNum);
        IPage<UserGroupVo> ipage = baseMapper.selectUserGroupList(new Page<>(pageNum, 10), userId);
        return ipage.getRecords();
    }

    @Override
    public GroupVo getGroupVo(int groupId) {
        log.debug("获取群聊信息Vo,groupId:{}", groupId);
        return baseMapper.selectGroupVoById(groupId);
    }

    @Override
    public boolean updateUserGroupInfo(int userId, UserGroupUpdateDto userGroupUpdateDto) {
        SysGroup sysGroup = baseMapper.selectById(userGroupUpdateDto.getGroupId());
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("更新用户群组信息,userId:{},userGroupUpdateDto:{}", userId, userGroupUpdateDto);
        LambdaUpdateWrapper<SysGroupMember> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysGroupMember::getUserId, userId)
                .eq(SysGroupMember::getGroupId, userGroupUpdateDto.getGroupId())
                .set(SysGroupMember::isMuted, userGroupUpdateDto.isMuted())
                .set(SysGroupMember::getGroupRemark, userGroupUpdateDto.getGroupRemark());
        return sysGroupMemberMapper.update(null, updateWrapper) > 0;
    }

    @Nullable
    @Override
    public String updateAvatar(int userId, int groupId, MultipartFile file) {
        if (file == null) return null;
        if (!FileUtils.isImage(file.getOriginalFilename()))
            throw new CommonException(CommonExceptionEnum.FILE_TYPE_NOT_SUPPORTED); // 文件类型不支持
        if ((file.getSize() / 1024) > groupProperties.getAvatarMaxSize())
            throw new CommonException(CommonExceptionEnum.FILE_SIZE_EXCEEDED); // 头像文件过大
        SysGroup group = baseMapper.selectById(groupId);
        if (group == null) return null;
        if (group.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("更新群聊头像,userId:{},groupId:{}", userId, groupId);
        String fileType = FileUtils.getFileType(file.getOriginalFilename());
        try {
            byte[] bytes = file.getInputStream().readAllBytes();
            String checkSum = FileUtils.getCheckSum(bytes);
            if (checkSum.isEmpty())
                throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
            String avatarPath = "/" + groupProperties.getAvatarDir() + "/" + checkSum + "." + fileType;
            if (minioService.getObject(avatarPath) != null) {
                // 文件存在,更新数据库
                baseMapper.updateAvatar(groupId, avatarPath);
            } else {
                // 文件不存在,上传文件
                if (minioService.upload(bytes, avatarPath)) {
                    // 上传成功
                    baseMapper.updateAvatar(groupId, avatarPath);
                } else
                    return null;
            }
            // 删除历史头像
            if (!group.getAvatar().equals("/" + groupProperties.getDefaultAvatarPath() + "/" + groupProperties.getDefaultAvatarName())
                    && baseMapper.getSameAvatarCount(group.getAvatar()) == 0) {
                // 该头像没有其他用户正在使用且非默认头像时，可以删除
                if (minioService.deleteObject(group.getAvatar())) {
                    log.debug("删除群聊历史头像成功，文件路径：{}", group.getAvatar());
                } else {
                    log.error("删除群聊历史头像失败，文件路径：{}", group.getAvatar());
                }
            }
            return avatarPath;
        } catch (IOException e) {
            log.error("文件上传异常", e);
            return null;
        }
    }

    @Override
    public boolean updateGroupInfo(int groupId, GroupUpdateDto groupUpdateDto) {
        if (groupId <= 0) return false;
        SysGroup sysGroup = baseMapper.selectById(groupId);
        if (sysGroup == null)
            return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        if (!GroupUtils.isValidGroupName(groupUpdateDto.getGroupName()))
            throw new GroupException(GroupExceptionEnum.INVALID_GROUP_NAME);
        if (!GroupUtils.isValidDescription(groupUpdateDto.getGroupDesc()))
            throw new GroupException(GroupExceptionEnum.INVALID_GROUP_DESCRIPTION);
        log.debug("更新群聊信息,groupId:{},groupUpdateDto:{}", groupId, groupUpdateDto);
        return lambdaUpdate().eq(SysGroup::getGroupId, groupId)
                .set(SysGroup::getGroupName, groupUpdateDto.getGroupName())
                .set(SysGroup::getGroupDesc, groupUpdateDto.getGroupDesc())
                .update();
    }

    @Transactional
    @Override
    public boolean disbandGroup(int groupId) {
        if (groupId <= 0) return false;
        SysGroupMember groupLeader = sysGroupMemberMapper.selectGroupLeader(groupId);
        SysGroup sysGroup = baseMapper.selectById(groupId);
        if (groupLeader == null || sysGroup == null) return false;
        if (sysGroup.getStatus() == GroupStatus.DISBAND)
            throw new GroupException(GroupExceptionEnum.GROUP_DISBAND);
        log.debug("解散群聊,groupId:{}", groupId);
        // 更新群组状态
        sysGroup.setStatus(GroupStatus.DISBAND);
        baseMapper.updateById(sysGroup);
        // 发送解散通知
        SysGroupNotification notification = new SysGroupNotification();
        notification.setGroupId(groupId);
        notification.setOperatorId(groupLeader.getUserId());
        notification.setType(GroupNotificationType.DISBAND);
        sysGroupNotificationMapper.insert(notification);
        webSocketService.publishMessage(WSMessage.command(groupId, ChatType.GROUP,
                MessageCommand.GROUP_DISBAND,
                List.of(String.valueOf(groupLeader.getUserId()))));
        return true;
    }
}