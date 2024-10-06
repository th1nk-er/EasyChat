package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.domain.dto.GroupInvitationRequestDto;
import top.th1nk.easychat.domain.dto.UserGroupUpdateDto;
import top.th1nk.easychat.domain.vo.GroupAdminInvitationVo;
import top.th1nk.easychat.domain.vo.GroupInvitationVo;
import top.th1nk.easychat.domain.vo.GroupMemberInfoVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;
import top.th1nk.easychat.service.SysGroupInvitationService;
import top.th1nk.easychat.service.SysGroupMemberService;
import top.th1nk.easychat.service.SysGroupService;

import java.util.List;

@RestController
@RequestMapping("/group")
@Tag(name = "群聊模块", description = "群聊API")
public class GroupController {
    @Resource
    private SysGroupService sysGroupService;
    @Resource
    private SysGroupInvitationService sysGroupInvitationService;
    @Resource
    private SysGroupMemberService sysGroupMemberService;

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

    @Operation(summary = "获取用户群聊邀请列表", description = "获取用户群聊邀请列表")
    @GetMapping("/invitation/list/{userId}/{pageNum}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<List<GroupInvitationVo>> getGroupInvitationList(@PathVariable int userId, @PathVariable int pageNum) {
        return Response.ok(sysGroupInvitationService.getUserGroupInvitationList(userId, pageNum));
    }

    @Operation(summary = "处理群聊邀请", description = "处理群聊邀请")
    @PostMapping("/invitation")
    @PreAuthorize("hasAuthority('USER:' + #groupInvitationRequestDto.getUserId())")
    public Response<?> handleInvitation(@RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        if (groupInvitationRequestDto.isAccept()) {
            if (sysGroupInvitationService.userAcceptInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        } else {
            if (sysGroupInvitationService.userRejectInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "获取用户管理的群聊的邀请列表", description = "获取用户管理的群聊的邀请列表")
    @GetMapping("/invitation/manage/list/{userId}/{pageNum}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<List<GroupAdminInvitationVo>> getAdminGroupInvitationList(@PathVariable int userId, @PathVariable int pageNum) {
        return Response.ok(sysGroupInvitationService.getAdminGroupInvitationList(userId, pageNum));
    }

    @Operation(summary = "群组管理员处理群聊邀请", description = "群组管理员处理群聊邀请")
    @PostMapping("/invitation/manage")
    @PreAuthorize("hasAuthority('GROUP_ADMIN:' + #groupInvitationRequestDto.getGroupId())")
    public Response<?> adminHandleInvitation(@RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        if (groupInvitationRequestDto.isAccept()) {
            if (sysGroupInvitationService.adminAcceptInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        } else {
            if (sysGroupInvitationService.adminRejectInvitation(groupInvitationRequestDto.getUserId(), groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "获取群聊信息", description = "获取群聊信息")
    @GetMapping("/info/{groupId}")
    public Response<?> getGroupInfo(@PathVariable int groupId) {
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
}
