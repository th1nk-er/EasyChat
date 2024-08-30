package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.AddFriendDto;
import top.th1nk.easychat.domain.dto.FriendRequestHandleDto;
import top.th1nk.easychat.domain.dto.UserFriendUpdateDto;
import top.th1nk.easychat.domain.vo.FriendListVo;
import top.th1nk.easychat.domain.vo.FriendRequestListVo;
import top.th1nk.easychat.domain.vo.UserFriendVo;
import top.th1nk.easychat.exception.enums.UserFriendExceptionEnum;
import top.th1nk.easychat.exception.UserFriendException;
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
    public Response<?> request(@RequestBody AddFriendDto addFriendDto) {
        if (!sysUserFriendService.sendAddRequest(addFriendDto))
            throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
        return Response.ok();
    }

    @Operation(summary = "获取好友申请列表", description = "获取好友申请列表")
    @GetMapping("/request/{page}")
    public Response<FriendRequestListVo> getFriendRequest(@PathVariable int page) {
        return Response.ok(sysUserAddFriendService.getFriendRequestList(page));
    }

    @Operation(summary = "处理好友申请", description = "处理好友申请")
    @PutMapping("/request")
    public Response<?> handleFriendRequest(@RequestBody FriendRequestHandleDto friendRequestHandleDto) {
        if (sysUserFriendService.handleAddRequest(friendRequestHandleDto))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.ADD_FRIEND_FAILED);
    }

    @Operation(summary = "获取好友列表", description = "获取好友列表")
    @GetMapping("/list/{page}")
    public Response<FriendListVo> getFriendList(@PathVariable int page) {
        return Response.ok(sysUserFriendService.getFriendList(page));
    }

    @Operation(summary = "获取好友信息", description = "获取好友信息")
    @GetMapping("/info/{friendId}")
    public Response<UserFriendVo> getFriendInfo(@PathVariable int friendId) {
        return Response.ok(sysUserFriendService.getFriendInfo(friendId));
    }

    @Operation(summary = "更新好友信息", description = "更新好友信息")
    @PutMapping("/info")
    public Response<?> updateFriendInfo(@RequestBody UserFriendUpdateDto userFriendUpdateDto) {
        if (sysUserFriendService.updateFriendInfo(userFriendUpdateDto))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.UPDATE_FRIEND_FAILED);
    }

    @Operation(summary = "删除好友", description = "删除好友")
    @DeleteMapping("/{friendId}")
    public Response<?> deleteFriend(@PathVariable int friendId) {
        if (sysUserFriendService.deleteFriend(friendId))
            return Response.ok();
        throw new UserFriendException(UserFriendExceptionEnum.DELETE_FRIEND_FAILED);
    }
}
