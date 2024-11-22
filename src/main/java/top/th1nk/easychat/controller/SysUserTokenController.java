package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
}
