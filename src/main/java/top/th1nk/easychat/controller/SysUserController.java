package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.LoginDto;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.dto.VerifyEmailDto;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.service.SysUserService;
import top.th1nk.easychat.utils.RandomUtils;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理API")
public class SysUserController {


    @Resource
    private SysUserService sysUserService;
    @Resource
    private EmailService emailService;

    @Operation(summary = "获取用户信息", description = "用户获取自身信息")
    @GetMapping("/info")
    public Response getUser() {
        // TODO 根据Token获取信息，并查询数据库获取Vo对象
        return Response.ok();
    }

    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public Response register(@RequestBody RegisterDto registerDto) {
        UserVo user = sysUserService.register(registerDto);
        return Response.ok(user);
    }

    @Operation(summary = "发送验证码邮件", description = "发送验证码邮件")
    @PostMapping("/verify-email")
    public Response sendVerifyCodeEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String code = RandomUtils.getRandomString(6).toUpperCase();
        emailService.sendVerifyCodeEmail(verifyEmailDto.getEmail(), code);
        emailService.saveVerifyCode(verifyEmailDto.getEmail(), code);
        return Response.ok();
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public Response login(@RequestBody LoginDto loginDto) {
        return Response.ok(sysUserService.login(loginDto));
    }
}
