package top.th1nk.easychat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.domain.Response;
import top.th1nk.easychat.domain.dto.*;
import top.th1nk.easychat.domain.vo.SearchUserVo;
import top.th1nk.easychat.domain.vo.StrangerVo;
import top.th1nk.easychat.domain.vo.UserLoginTokenVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.EmailActionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.LoginException;
import top.th1nk.easychat.exception.RegisterException;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;
import top.th1nk.easychat.exception.enums.RegisterExceptionEnum;
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

    @Operation(summary = "获取陌生人信息", description = "用户获取指定陌生人信息")
    @GetMapping("/info/{strangerId}")
    public Response<StrangerVo> getStranger(@PathVariable int strangerId) {
        return Response.ok(sysUserService.getStrangerInfo(strangerId));
    }

    @Operation(summary = "用户注册", description = "用户注册")
    @PostMapping("/register")
    public Response<UserVo> register(@RequestBody RegisterDto registerDto) {
        UserVo user = sysUserService.register(registerDto);
        return Response.ok(user);
    }

    @Operation(summary = "发送登录验证码邮件", description = "发送登录验证码邮件")
    @PostMapping("/email/login")
    public Response<?> sendLoginVerifyCodeEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String code = StringUtils.getRandomString(6).toUpperCase();
        if (!sysUserService.isEmailExist(verifyEmailDto.getEmail()))
            throw new LoginException(LoginExceptionEnum.EMAIL_NOT_REGISTER);
        if (emailService.isEmailSendFrequently(verifyEmailDto.getEmail(), EmailActionEnum.ACTION_LOGIN)) {
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_SEND__FREQUENTLY);
        }
        emailService.sendVerifyCodeEmail(verifyEmailDto.getEmail(), code, EmailActionEnum.ACTION_LOGIN);
        emailService.saveVerifyCode(verifyEmailDto.getEmail(), code, EmailActionEnum.ACTION_LOGIN);
        return Response.ok();
    }

    @Operation(summary = "发送注册验证码邮件", description = "发送注册验证码邮件")
    @PostMapping("/email/register")
    public Response<?> sendRegisterVerifyCodeEmail(@RequestBody VerifyEmailDto verifyEmailDto) {
        String code = StringUtils.getRandomString(6).toUpperCase();
        if (sysUserService.isEmailExist(verifyEmailDto.getEmail()))
            throw new RegisterException(RegisterExceptionEnum.EMAIL_EXIST);
        if (emailService.isEmailSendFrequently(verifyEmailDto.getEmail(), EmailActionEnum.ACTION_REGISTER)) {
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_SEND__FREQUENTLY);
        }
        emailService.sendVerifyCodeEmail(verifyEmailDto.getEmail(), code, EmailActionEnum.ACTION_REGISTER);
        emailService.saveVerifyCode(verifyEmailDto.getEmail(), code, EmailActionEnum.ACTION_REGISTER);
        return Response.ok();
    }

    @Operation(summary = "用户登录", description = "用户登录")
    @PostMapping("/login")
    public Response<UserLoginTokenVo> login(@RequestBody LoginDto loginDto) {
        return Response.ok(sysUserService.login(loginDto));
    }

    @Operation(summary = "用户登出", description = "用户登出")
    @PostMapping("/logout")
    public Response<?> logout() {
        String userTokenString = RequestUtils.getUserTokenString();
        if (userTokenString.isEmpty()) return Response.error();
        if (sysUserService.logout(userTokenString))
            return Response.ok();
        else return Response.error();
    }

    @Operation(summary = "用户搜索", description = "根据关键字搜索用户")
    @GetMapping("/search")
    public Response<SearchUserVo> search(@RequestParam("keyword") String keyword, @RequestParam("page") int page) {
        return Response.ok(sysUserService.searchUser(keyword, page));
    }

    @Operation(summary = "发送修改密码验证码邮件", description = "发送修改密码验证码邮件")
    @PostMapping("/email/change-password")
    public Response<?> sendChangePasswordVerifyCodeEmail() {
        String code = StringUtils.getRandomString(6).toUpperCase();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getEmail() == null)
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (!sysUserService.isEmailExist(userVo.getEmail()))
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (emailService.isEmailSendFrequently(userVo.getEmail(), EmailActionEnum.ACTION_CHANGE_PASSWORD)) {
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_SEND__FREQUENTLY);
        }
        emailService.sendVerifyCodeEmail(userVo.getEmail(), code, EmailActionEnum.ACTION_CHANGE_PASSWORD);
        emailService.saveVerifyCode(userVo.getEmail(), code, EmailActionEnum.ACTION_CHANGE_PASSWORD);
        return Response.ok();
    }

    @Operation(summary = "修改密码", description = "修改密码")
    @PutMapping("/password")
    @PreAuthorize("hasAuthority('USER:' + #updatePasswordDto.getUserId())")
    public Response<?> updatePassword(@RequestBody UpdatePasswordDto updatePasswordDto) {
        if (sysUserService.updatePassword(updatePasswordDto)) {
            return Response.ok();
        }
        return Response.error("密码修改失败");
    }

    @Operation(summary = "修改头像", description = "修改头像")
    @PostMapping("/avatar/{userId}")
    @PreAuthorize("hasAuthority('USER:' + #userId)")
    public Response<?> updateAvatar(@RequestParam("file") MultipartFile file, @PathVariable int userId) {
        String avatarPath = sysUserService.updateAvatar(userId, file);
        if (avatarPath != null)
            return Response.ok(avatarPath);
        else
            throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
    }

    @Operation(summary = "修改用户信息", description = "修改用户信息")
    @PutMapping("/info")
    @PreAuthorize("hasAuthority('USER:' + #updateUserInfoDto.getUserId())")
    public Response<?> updateUserInfo(@RequestBody UpdateUserInfoDto updateUserInfoDto) {
        if (sysUserService.updateUserInfo(updateUserInfoDto)) {
            return Response.ok();
        }
        return Response.error("用户信息修改失败");
    }

    @Operation(summary = "发送修改邮箱验证码邮件", description = "发送修改邮箱验证码邮件")
    @PostMapping("/email/change-email")
    public Response<?> sendChangeEmailVerifyCodeEmail() {
        String code = StringUtils.getRandomString(6).toUpperCase();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getEmail() == null)
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (!sysUserService.isEmailExist(userVo.getEmail()))
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (emailService.isEmailSendFrequently(userVo.getEmail(), EmailActionEnum.ACTION_CHANGE_EMAIL)) {
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_SEND__FREQUENTLY);
        }
        emailService.sendVerifyCodeEmail(userVo.getEmail(), code, EmailActionEnum.ACTION_CHANGE_EMAIL);
        emailService.saveVerifyCode(userVo.getEmail(), code, EmailActionEnum.ACTION_CHANGE_EMAIL);
        return Response.ok();
    }

    @Operation(summary = "发送新邮箱验证邮件", description = "发送新邮箱验证邮件")
    @PostMapping("/email/email-verify")
    public Response<?> sendEmailVerifyCodeEmail() {
        String code = StringUtils.getRandomString(6).toUpperCase();
        UserVo userVo = jwtUtils.parseToken(RequestUtils.getUserTokenString());
        if (userVo == null || userVo.getEmail() == null)
            throw new CommonException(CommonExceptionEnum.USER_NOT_FOUND);
        if (sysUserService.isEmailExist(userVo.getEmail()))
            throw new CommonException(CommonExceptionEnum.EMAIL_EXIST);
        if (emailService.isEmailSendFrequently(userVo.getEmail(), EmailActionEnum.ACTION_EMAIL_VERIFY)) {
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_SEND__FREQUENTLY);
        }
        emailService.sendVerifyCodeEmail(userVo.getEmail(), code, EmailActionEnum.ACTION_EMAIL_VERIFY);
        emailService.saveVerifyCode(userVo.getEmail(), code, EmailActionEnum.ACTION_EMAIL_VERIFY);
        return Response.ok();
    }

    @Operation(summary = "修改邮箱", description = "修改邮箱")
    @PutMapping("/email")
    @PreAuthorize("hasAuthority('USER:' + #updateEmailDto.getUserId())")
    public Response<?> updateEmail(@RequestBody UpdateEmailDto updateEmailDto) {
        if (sysUserService.updateEmail(updateEmailDto)) {
            return Response.ok();
        }
        return Response.error("邮箱修改失败");
    }
}
