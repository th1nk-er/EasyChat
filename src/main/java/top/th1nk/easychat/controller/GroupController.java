package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.CreateGroupDto;
import top.th1nk.easychat.domain.dto.GroupInvitationRequestDto;
import top.th1nk.easychat.domain.vo.GroupInvitationVo;
import top.th1nk.easychat.domain.vo.UserGroupVo;
import top.th1nk.easychat.service.SysGroupInvitationService;
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

    @Operation(summary = "创建群聊", description = "创建群聊")
    @PostMapping("/create")
    public Response<?> createGroup(@RequestBody CreateGroupDto createGroupDto) {
        if (sysGroupService.createGroup(createGroupDto))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "获取用户群聊列表", description = "获取用户群聊列表")
    @GetMapping("/list/{pageNum}")
    public Response<List<UserGroupVo>> getGroupList(@PathVariable int pageNum) {
        return Response.ok(sysGroupService.getUserGroupList(pageNum));
    }

    @Operation(summary = "获取用户群聊邀请列表", description = "获取用户群聊邀请列表")
    @GetMapping("/invitation/list/{pageNum}")
    public Response<List<GroupInvitationVo>> getGroupInvitationList(@PathVariable int pageNum) {
        return Response.ok(sysGroupInvitationService.getUserGroupInvitationList(pageNum));
    }

    @Operation(summary = "处理群聊邀请", description = "处理群聊邀请")
    @PostMapping("/invitation")
    public Response<?> handleInvitation(@RequestBody GroupInvitationRequestDto groupInvitationRequestDto) {
        if (groupInvitationRequestDto.isAccept()) {
            if (sysGroupInvitationService.userAcceptInvitation(groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        } else {
            if (sysGroupInvitationService.userRejectInvitation(groupInvitationRequestDto.getGroupId()))
                return Response.ok();
        }
        return Response.error();
    }

    @Operation(summary = "群组管理员处理群聊邀请", description = "群组管理员处理群聊邀请")
    @PostMapping("/invitation/manage")
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
}
