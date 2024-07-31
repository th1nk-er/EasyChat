package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.LoginDto;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.dto.UserTokenDto;
import top.th1nk.easychat.domain.dto.VerifyEmailDto;
import top.th1nk.easychat.domain.vo.SearchUserVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.LoginExceptionEnum;
import top.th1nk.easychat.enums.RegisterExceptionEnum;
import top.th1nk.easychat.exception.LoginException;
import top.th1nk.easychat.exception.RegisterException;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.service.SysUserService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;
import top.th1nk.easychat.utils.StringUtils;


@RestController
@RequestMapping("/user")
@Tag(name = "用户管理", description = "用户管理API")
public class SysUserController {


    @Resource
    private SysUserService sysUserService;
    @Resource
    private EmailService emailService;
    @Resource
    private JwtUtils jwtUtils;

    @Operation(summary = "获取用户信息", description = "用户获取自身信息")
    @GetMapping("/info")
    public Response<UserVo> getUser() {
        return Response.ok(jwtUtils.parseToken(RequestUtils.getUserTokenString()));
    }

    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public Response<UserVo> register(@RequestBody RegisterDto registerDto) {
        UserVo user = sysUserService.register(registerDto);
        return Response.ok(user);
    }

    @Operation(summary = "发送登录验证码邮件", description = "发送登录验证码邮件")
    @PostMapping("/verify-email")
    public Response<?> sendLoginVerifyCodeEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String code = StringUtils.getRandomString(6).toUpperCase();
        if (!sysUserService.isEmailExist(verifyEmailDto.getEmail()))
            throw new LoginException(LoginExceptionEnum.EMAIL_NOT_REGISTER);
        emailService.beforeSendVerifyCode(verifyEmailDto.getEmail());
        emailService.sendVerifyCodeEmail(verifyEmailDto.getEmail(), code);
        emailService.saveVerifyCode(verifyEmailDto.getEmail(), code);
        return Response.ok();
    }

    @Operation(summary = "发送注册验证码邮件", description = "发送注册验证码邮件")
    @PostMapping("/register/verify-email")
    public Response<?> sendRegisterVerifyCodeEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String code = StringUtils.getRandomString(6).toUpperCase();
        if (sysUserService.isEmailExist(verifyEmailDto.getEmail()))
            throw new RegisterException(RegisterExceptionEnum.EMAIL_EXIST);
        emailService.beforeSendVerifyCode(verifyEmailDto.getEmail());
        emailService.sendVerifyCodeEmail(verifyEmailDto.getEmail(), code);
        emailService.saveVerifyCode(verifyEmailDto.getEmail(), code);
        return Response.ok();
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public Response<UserTokenDto> login(@RequestBody LoginDto loginDto) {
        return Response.ok(sysUserService.login(loginDto));
    }

    @Operation(summary = "用户搜索", description = "根据关键字搜索用户")
    @GetMapping("/search")
    public Response<SearchUserVo> search(@RequestParam("keyword") String keyword, @RequestParam("page") int page) {
        return Response.ok(sysUserService.searchUser(keyword, page));
    }
}
