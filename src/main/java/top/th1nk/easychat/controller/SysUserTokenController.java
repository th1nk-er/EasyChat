package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.vo.UserTokenVo;
import top.th1nk.easychat.service.SysUserTokenService;

import java.util.List;

@RestController
@RequestMapping("/security")
@Tag(name = "用户Token管理", description = "用户Token管理API")
public class SysUserTokenController {
    @Resource
    private SysUserTokenService sysUserTokenService;

    @Operation(summary = "获取用户tokenVo列表", description = "根据用户id获取用户tokenVo列表")
    @GetMapping("/token/list/user/{userId}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<List<UserTokenVo>> getTokenVoList(@PathVariable int userId) {
        return Response.ok(sysUserTokenService.getUserTokenVoList(userId));
    }

    @Operation(summary = "根据ID过期token", description = "根据token主键过期token")
    @PutMapping("/token/expire/{userId}/{tokenId}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<?> expireToken(@PathVariable int userId, @PathVariable int tokenId) {
        boolean success = sysUserTokenService.expireTokenById(userId, tokenId);
        if (success) return Response.ok();
        else return Response.error();
    }
}
