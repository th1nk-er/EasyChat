package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.dto.UserFriendUpdateDto;
import top.th1nk.easychat.domain.vo.FriendListVo;
import top.th1nk.easychat.domain.vo.FriendRequestListVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;
import top.th1nk.easychat.exception.UserFriendException;
import top.th1nk.easychat.exception.enums.UserFriendExceptionEnum;
import top.th1nk.easychat.service.SysUserAddFriendService;
import top.th1nk.easychat.service.SysUserFriendService;

@RestController
@RequestMapping("/friend")
@Tag(name = "用户好友管理", description = "用户好友管理API")
public class SysUserFriendController {

    @Resource
    private SysUserFriendService sysUserFriendService;
    @Resource
    private SysUserAddFriendService sysUserAddFriendService;

    @Operation(summary = "发送好友申请", description = "向陌生人发送好友申请")
    @PostMapping("/request")
    @PreAuthorize("hasAuthority('USER:' + #addFriendDto.getUserId())")
    public Response<?> request(@RequestBody AddFriendDto addFriendDto) {
        if (!sysUserFriendService.sendAddRequest(addFriendDto))
            throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
        return Response.ok();
    }

    @Operation(summary = "获取好友申请列表", description = "获取好友申请列表")
    @GetMapping("/request/{userId}/{page}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<FriendRequestListVo> getFriendRequest(@PathVariable int userId, @PathVariable int page) {
        return Response.ok(sysUserAddFriendService.getFriendRequestList(userId, page));
    }

    @Operation(summary = "处理好友申请", description = "处理好友申请")
    @PutMapping("/request")
    @PreAuthorize("hasAuthority('USER:' + #friendRequestHandleDto.getUserId())")
    public Response<?> handleFriendRequest(@RequestBody FriendRequestHandleDto friendRequestHandleDto) {
        if (sysUserFriendService.handleAddRequest(friendRequestHandleDto))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
    }

    @Operation(summary = "获取好友列表", description = "获取好友列表")
    @GetMapping("/list/{userId}/{page}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<FriendListVo> getFriendList(@PathVariable int userId, @PathVariable int page) {
        return Response.ok(sysUserFriendService.getFriendList(userId, page));
    }

    @Operation(summary = "获取好友信息", description = "获取好友信息")
    @GetMapping("/info/{userId}/{friendId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('FRIEND:' + #friendId)")
    public Response<UserFriendVo> getFriendInfo(@PathVariable int userId, @PathVariable int friendId) {
        return Response.ok(sysUserFriendService.getFriendInfo(userId, friendId));
    }

    @Operation(summary = "更新好友信息", description = "更新好友信息")
    @PutMapping("/info/{userId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('FRIEND:' + #userFriendUpdateDto.getFriendId())")
    public Response<?> updateFriendInfo(@PathVariable int userId, @RequestBody UserFriendUpdateDto userFriendUpdateDto) {
        if (sysUserFriendService.updateFriendInfo(userId, userFriendUpdateDto))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.UPDATE_FRIEND_FAILED);
    }

    @Operation(summary = "删除好友", description = "删除好友")
    @DeleteMapping("/{userId}/{friendId}")
    @PreAuthorize("hasAuthority('USER:' + #userId) and hasAuthority('FRIEND:' + #friendId)")
    public Response<?> deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        if (sysUserFriendService.deleteFriend(userId, friendId))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.DELETE_FRIEND_FAILED);
    }
}
