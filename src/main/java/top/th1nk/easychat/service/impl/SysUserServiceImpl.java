package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.security.EmailAuthenticationToken;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserToken;
import top.th1nk.easychat.domain.dto.LoginDto;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.dto.UserTokenDto;
import top.th1nk.easychat.domain.vo.SearchUserVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.CommonExceptionEnum;
import top.th1nk.easychat.enums.LoginExceptionEnum;
import top.th1nk.easychat.enums.LoginType;
import top.th1nk.easychat.enums.RegisterExceptionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.LoginException;
import top.th1nk.easychat.exception.RegisterException;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.service.SysUserService;
import top.th1nk.easychat.service.SysUserTokenService;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;
import top.th1nk.easychat.utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author th1nk
 * @description 针对表【ec_user】的数据库操作Service实现
 * @createDate 2024-07-08 14:05:15
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    private EmailService emailService;
    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SysUserTokenService sysUserTokenService;

    @Override
    public UserVo register(RegisterDto registerDto) {
        if (registerDto == null) throw new CommonException(CommonExceptionEnum.PARAM_INVALID);
        if (!UserUtils.isValidUsername(registerDto.getUsername()))
            throw new CommonException(CommonExceptionEnum.USERNAME_INVALID); // 用户名不合法
        if (!UserUtils.isValidPassword(registerDto.getPassword()))
            throw new CommonException(CommonExceptionEnum.PASSWORD_INVALID); // 密码不合法
        if (!UserUtils.isValidEmail(registerDto.getEmail()))
            throw new CommonException(CommonExceptionEnum.EMAIL_INVALID); // 邮箱不合法
        if (isUsernameExist(registerDto.getUsername()))
            throw new RegisterException(RegisterExceptionEnum.USERNAME_EXIST); // 用户名已存在
        if (isEmailExist(registerDto.getEmail()))
            throw new RegisterException(RegisterExceptionEnum.EMAIL_EXIST); // 邮箱已存在
        if (!emailService.verifyCode(registerDto.getEmail(), registerDto.getVerifyCode()))
            throw new RegisterException(RegisterExceptionEnum.EMAIL_VERIFY_CODE_ERROR); // 验证码错误
        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(registerDto, user);
        user.setPassword(UserUtils.encryptPassword(registerDto.getPassword()));
        user.setNickname(registerDto.getUsername());
        user.setRegisterIp(RequestUtils.getClientIp());
//        user.setAvatar(""); // TODO 设置默认头像地址
        log.info("注册用户:{}", user);
        // 插入到数据库
        if (baseMapper.insert(user) != 1)
            throw new RegisterException(RegisterExceptionEnum.REGISTER_FAILED); // 注册失败
        // 返回用户Vo
        return UserUtils.userToVo(baseMapper.getByUsername(registerDto.getUsername()));
    }

    @Override
    public UserTokenDto login(LoginDto loginDto) {
        SysUser user;
        UserVo userVo;
        Authentication authenticationToken;
        if (loginDto.getType() == LoginType.EMAIL) {
            // 邮箱登录
            if (!UserUtils.isValidEmail(loginDto.getEmail()))
                throw new CommonException(CommonExceptionEnum.EMAIL_INVALID);
            user = baseMapper.getByEmail(loginDto.getEmail());
            if (user == null)
                throw new LoginException(LoginExceptionEnum.EMAIL_NOT_REGISTER); // 邮箱未注册
            authenticationToken = new EmailAuthenticationToken(loginDto.getEmail(), loginDto.getVerifyCode());
        } else {
            // 密码登录
            authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            user = baseMapper.getByUsername(loginDto.getUsername());
        }
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            // 登录成功
            userVo = UserUtils.userToVo(user);
            userVo.setLoginType(loginDto.getType());
            baseMapper.updateLoginIp(user.getUsername(), RequestUtils.getClientIp()); // 更新登录IP
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (AuthenticationException e) {
            log.info("登录失败 登陆方式:{} 用户名:{} 邮箱:{}", loginDto.getType().getDesc(), loginDto.getUsername(), loginDto.getEmail());
            if (loginDto.getType() == LoginType.EMAIL)
                throw new LoginException(LoginExceptionEnum.EMAIL_VERIFY_CODE_ERROR); // 邮箱验证码错误
            else
                throw new LoginException(LoginExceptionEnum.USERNAME_OR_PASSWORD_ERROR); // 用户名或密码错误
        }
        // 生成Token
        SysUserToken sysUserToken = jwtUtils.generateToken(userVo);
        sysUserTokenService.saveUserToken(sysUserToken);
        UserTokenDto userTokenDto = new UserTokenDto();
        BeanUtils.copyProperties(sysUserToken, userTokenDto);
        return userTokenDto;
    }

    @Override
    public boolean isUsernameExist(String username) {
        return baseMapper.getByUsername(username) != null;
    }

    @Override
    public boolean isEmailExist(String email) {
        return baseMapper.getByEmail(email) != null;
    }

    @Override
    public SearchUserVo searchUser(String keyword, int page) {
        SearchUserVo result = new SearchUserVo();
        result.setPageSize(10L);
        result.setTotal(0);
        result.setRecords(List.of());
        if (keyword == null || keyword.isEmpty()) return result;

        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.like(SysUser::getUsername, keyword).or().like(SysUser::getNickname, keyword);
        IPage<SysUser> iPage = baseMapper.selectPage(new Page<>(page, result.getPageSize()), qw);

        if (iPage.getTotal() == 0) return result;

        List<SearchUserVo.Record> records = new ArrayList<>();
        iPage.getRecords().forEach(sysUser -> {
            SearchUserVo.Record record = new SearchUserVo.Record();
            record.setId(sysUser.getId());
            record.setSex(sysUser.getSex());
            record.setUsername(sysUser.getUsername());
            record.setNickname(sysUser.getNickname());
            records.add(record);
        });
        result.setRecords(records);
        result.setTotal(iPage.getTotal());
        return result;
    }

}