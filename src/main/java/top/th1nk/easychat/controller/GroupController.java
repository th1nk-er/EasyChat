package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.*;
import top.th1nk.easychat.domain.vo.*;
import top.th1nk.easychat.enums.UserRole;
import top.th1nk.easychat.service.*;

import java.util.List;

@RestController
@RequestMapping("/group")
@Tag(name = "群聊模块", description = "群聊API")
public class GroupController {
    @Resource
    private SysGroupService sysGroupService;
    @Resource
    private SysGroupNotificationService sysGroupNotificationService;
    @Resource
    private SysGroupMemberService sysGroupMemberService;
    @Resource
    private SysGroupMemberIgnoredService sysGroupMemberIgnoredService;
    @Resource
    private SysGroupMemberMutedService sysGroupMemberMutedService;

    @Operation(summary = "创建群聊", description = "创建群聊")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('USER:' + #createGroupDto.getUserId())")
    public Response<?> createGroup(@RequestBody CreateGroupDto createGroupDto) {
        if (sysGroupService.createGroup(createGroupDto))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "获取用户群聊列表", description = "获取用户群聊列表")
    @GetMapping("/list/{userId}/{pageNum}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<List<UserGroupVo>> getGroupList(@PathVariable int userId, @PathVariable int pageNum) {
        return Response.ok(sysGroupService.getUserGroupList(userId, pageNum));
    }

    @Operation(summary = "获取用户群聊通知列表", description = "获取用户群聊通知列表")
    @GetMapping("/notification/list/{userId}/{pageNum}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<List<GroupNotificationVo>> getGroupNotificationList(@PathVariable int userId, @PathVariable int pageNum) {
        return Response.ok(sysGroupNotificationService.getUserGroupNotificationList(userId, pageNum));
    }

    @Operation(summary = "处理群聊邀请", description = "处理群聊邀请")
    @PostMapping("/invitation")
    @PreAuthorize("hasAuthority('USER:' + #groupInvitationRequestDto.getUserId())")
    public Response<?> handleInvitation(@RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        if (groupInvitationRequestDto.isAccept()) {
            if (sysGroupNotificationService.userAcceptInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        } else {
            if (sysGroupNotificationService.userRejectInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "群组管理员处理群聊邀请", description = "群组管理员处理群聊邀请")
    @PostMapping("/invitation/manage")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupInvitationRequestDto.getGroupId())")
    public Response<?> adminHandleInvitation(@RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        if (groupInvitationRequestDto.isAccept()) {
            if (sysGroupNotificationService.adminAcceptInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        } else {
            if (sysGroupNotificationService.adminRejectInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "获取群聊信息", description = "获取群聊信息")
    @GetMapping("/info/{groupId}")
    public Response<GroupVo> getGroupInfo(@PathVariable int groupId) {
        return Response.ok(sysGroupService.getGroupVo(groupId));
    }

    @Operation(summary = "更新用户用户群组信息", description = "更新用户用户群组信息")
    @PutMapping("/update/user/{userId}")
    @PreAuthorize("hasAuthority('USER:'+ #userId) and hasAuthority('GROUP:' + #userGroupUpdateDto.getGroupId())")
    public Response<?> updateUserGroupInfo(@PathVariable int userId, @RequestBody UserGroupUpdateDto userGroupUpdateDto) {
        if (sysGroupService.updateUserGroupInfo(userId, userGroupUpdateDto))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "获取群组成员的详细信息", description = "获取群组成员的详细信息")
    @GetMapping("/{groupId}/member/{userId}")
    @PreAuthorize("hasAuthority('GROUP:' + #groupId)")
    public Response<GroupMemberInfoVo> getGroupMemberInfo(@PathVariable int userId, @PathVariable int groupId) {
        return Response.ok(sysGroupMemberService.getGroupMemberInfo(userId, groupId));
    }

    @Operation(summary = "退出群组", description = "退出群组")
    @DeleteMapping("/quit/{userId}/{groupId}")
    @PreAuthorize("hasAnyAuthority('USER:' + #userId) and hasAuthority('GROUP:' + #groupId)")
    public Response<?> quitGroup(@PathVariable int userId, @PathVariable int groupId) {
        if (sysGroupMemberService.quitGroup(userId, groupId))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "踢出群组成员", description = "踢出群组成员")
    @DeleteMapping("/kick/{userId}/{groupId}/{memberId}")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupId) and hasAuthority('USER:' + #userId)")
    public Response<?> kickMember(@PathVariable int userId, @PathVariable int groupId, @PathVariable int memberId) {
        if (sysGroupMemberService.kickMember(userId, groupId, memberId))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "获取群组成员详细信息列表", description = "获取群组成员详细信息列表")
    @GetMapping("/{groupId}/member/list/{pageNum}")
    @PreAuthorize("hasAuthority('GROUP:' + #groupId)")
    public Response<List<GroupMemberInfoVo>> getGroupMemberList(@PathVariable int groupId, @PathVariable int pageNum) {
        return Response.ok(sysGroupMemberService.getGroupMemberInfoVoList(groupId, pageNum));
    }

    @Operation(summary = "邀请用户加入群聊", description = "邀请用户加入群聊")
    @PostMapping("/invite/{userId}/{groupId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('GROUP:' + #groupId)")
    public Response<?> inviteMembers(@PathVariable int userId, @PathVariable int groupId, @RequestBody List<Integer> memberIds) {
        if (sysGroupNotificationService.inviteMembers(userId, groupId, memberIds))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "更新群组成员昵称", description = "更新群组成员昵称")
    @PutMapping("/{groupId}/user/{userId}/nickname")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupId) or hasAuthority('USER:' + #userId)")
    public Response<?> updateUserGroupNickname(@PathVariable int groupId, @PathVariable int userId, @RequestBody UpdateGroupMemberDto dto) {
        if (sysGroupMemberService.updateUserGroupNickname(userId, groupId, dto.getNickname()))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "更新群成员的身份", description = "更新群成员的身份")
    @PutMapping("/{groupId}/member/{memberId}/role")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupId) and hasAuthority('USER:' + #dto.getUserId())")
    public Response<?> updateMemberRole(@PathVariable int groupId, @PathVariable int memberId, @RequestBody UpdateGroupMemberDto dto) {
        if (dto.getRole() == null || dto.getUserId() == null) return Response.error();
        if (dto.getRole() == UserRole.ADMIN) {
            if (sysGroupMemberService.setMemberAsAdmin(dto.getUserId(), groupId, memberId))
                return Response.ok();
        } else if (dto.getRole() == UserRole.USER) {
            if (sysGroupMemberService.removeAdminRole(dto.getUserId(), groupId, memberId))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "获取用户对某个群聊中成员的屏蔽信息", description = "获取用户对某个群聊中成员的屏蔽信息")
    @GetMapping("/{groupId}/{userId}/ignored/members")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('GROUP:' + #groupId)")
    public Response<List<GroupMemberIgnoredVo>> getIgnoredMemberIds(@PathVariable int groupId, @PathVariable int userId) {
        return Response.ok(sysGroupMemberIgnoredService.getGroupIgnoredVo(userId, groupId));
    }

    @Operation(summary = "忽略群成员", description = "忽略群成员")
    @PostMapping("/{groupId}/{userId}/ignore/member/{memberId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('GROUP:' + #groupId)")
    public Response<?> ignoreMember(@PathVariable int groupId, @PathVariable int userId, @PathVariable int memberId) {
        if (sysGroupMemberIgnoredService.ignoreMemberForUser(userId, groupId, memberId))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "取消忽略群成员", description = "取消忽略群成员")
    @DeleteMapping("/{groupId}/{userId}/ignore/member/{memberId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('GROUP:' + #groupId)")
    public Response<?> cancelIgnoreMember(@PathVariable int groupId, @PathVariable int userId, @PathVariable int memberId) {
        if (sysGroupMemberIgnoredService.cancelIgnoreMemberForUser(userId, groupId, memberId))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "禁言群成员", description = "禁言群成员")
    @PostMapping("/mute/member")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #muteMemberDto.getGroupId()) and hasAuthority('USER:' + #muteMemberDto.getAdminId())")
    public Response<?> muteMember(@RequestBody MuteMemberDto muteMemberDto) {
        if (sysGroupMemberMutedService.muteMember
                (muteMemberDto.getGroupId(), muteMemberDto.getMemberId(), muteMemberDto.getAdminId(), muteMemberDto.getDuration()))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "解除禁言群成员", description = "解除禁言群成员")
    @DeleteMapping("/{groupId}/mute/member/{memberId}/{adminId}")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupId) and hasAuthority('USER:' + #adminId)")
    public Response<?> unmuteMember(@PathVariable int groupId, @PathVariable int memberId, @PathVariable int adminId) {
        if (sysGroupMemberMutedService.unmuteMember(groupId, memberId, adminId))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "获取群成员禁言状态", description = "获取群成员禁言状态")
    @GetMapping("/{groupId}/mute/status/member/{memberId}")
    @PreAuthorize("hasAuthority('GROUP:' + #groupId)")
    public Response<GroupMemberMuteVo> isMemberMute(@PathVariable int groupId, @PathVariable int memberId) {
        return Response.ok(sysGroupMemberMutedService.getMuteInfo(groupId, memberId));
    }
}
