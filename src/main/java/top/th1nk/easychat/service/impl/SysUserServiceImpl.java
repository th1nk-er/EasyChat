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
import org.springframework.web.multipart.MultipartFile;
import top.th1nk.easychat.config.easychat.UserProperties;
import top.th1nk.easychat.config.security.EmailAuthenticationToken;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.SysUserToken;
import top.th1nk.easychat.domain.dto.*;
import top.th1nk.easychat.domain.vo.SearchUserVo;
import top.th1nk.easychat.domain.vo.StrangerVo;
import top.th1nk.easychat.domain.vo.UserLoginTokenVo;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.EmailActionEnum;
import top.th1nk.easychat.enums.LoginType;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.DataException;
import top.th1nk.easychat.exception.LoginException;
import top.th1nk.easychat.exception.RegisterException;
import top.th1nk.easychat.exception.enums.CommonExceptionEnum;
import top.th1nk.easychat.exception.enums.DataExceptionEnum;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;
import top.th1nk.easychat.exception.enums.RegisterExceptionEnum;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.service.MinioService;
import top.th1nk.easychat.service.SysUserService;
import top.th1nk.easychat.service.SysUserTokenService;
import top.th1nk.easychat.utils.FileUtils;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.RequestUtils;
import top.th1nk.easychat.utils.UserUtils;

import java.io.IOException;
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
    @Resource
    private UserProperties userProperties;
    @Resource
    private MinioService minioService;

    @Override
    public UserVo getByUsername(String username) {
        log.debug("获取用户信息 用户名:{}", username);
        return UserUtils.userToVo(baseMapper.getByUsername(username));
    }

    @Override
    public UserVo getByEmail(String email) {
        log.debug("获取用户信息 邮箱:{}", email);
        return UserUtils.userToVo(baseMapper.getByEmail(email));
    }

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
        if (!emailService.verifyCode(registerDto.getEmail(), registerDto.getVerifyCode(), EmailActionEnum.ACTION_REGISTER))
            throw new RegisterException(RegisterExceptionEnum.EMAIL_VERIFY_CODE_ERROR); // 验证码错误
        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(registerDto, user);
        user.setPassword(UserUtils.encryptPassword(registerDto.getPassword()));
        user.setNickname(registerDto.getUsername());
        user.setRegisterIp(RequestUtils.getClientIp());
        user.setAvatar("/" + userProperties.getAvatarDir() + "/" + userProperties.getDefaultAvatarName());
        log.info("注册用户:{}", user);
        // 插入到数据库
        if (baseMapper.insert(user) != 1)
            throw new RegisterException(RegisterExceptionEnum.REGISTER_FAILED); // 注册失败
        // 返回用户Vo
        return UserUtils.userToVo(baseMapper.getByUsername(registerDto.getUsername()));
    }

    @Override
    public UserLoginTokenVo login(LoginDto loginDto) {
        log.debug("用户发起登录请求 登陆方式:{} 用户名:{} 邮箱:{}", loginDto.getType().getDesc(), loginDto.getUsername(), loginDto.getEmail());
        UserVo userVo;
        Authentication authenticationToken;
        if (loginDto.getType() == LoginType.EMAIL) {
            // 邮箱登录
            if (!UserUtils.isValidEmail(loginDto.getEmail()))
                throw new CommonException(CommonExceptionEnum.EMAIL_INVALID);
            userVo = this.getByEmail(loginDto.getEmail());
            if (userVo == null)
                throw new LoginException(LoginExceptionEnum.EMAIL_NOT_REGISTER); // 邮箱未注册
            userVo.setLoginType(LoginType.EMAIL);
            authenticationToken = new EmailAuthenticationToken(loginDto.getEmail(), loginDto.getVerifyCode());
        } else {
            // 密码登录
            authenticationToken = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
            userVo = this.getByUsername(loginDto.getUsername());
            if (userVo == null)
                throw new LoginException(LoginExceptionEnum.USERNAME_OR_PASSWORD_ERROR);
            userVo.setLoginType(LoginType.PASSWORD);
        }
        try {
            Authentication authenticate = authenticationManager.authenticate(authenticationToken);
            log.debug("用户登录成功 用户名:{}", userVo.getUsername());
            // 登录成功
            baseMapper.updateLoginIp(userVo.getUsername(), RequestUtils.getClientIp()); // 更新登录IP
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
        if (sysUserTokenService.saveUserToken(sysUserToken)) {
            UserLoginTokenVo userLoginTokenVo = new UserLoginTokenVo();
            BeanUtils.copyProperties(sysUserToken, userLoginTokenVo);
            return userLoginTokenVo;
        } else {
            throw new DataException(DataExceptionEnum.DATA_INSERT_FAILED);
        }
    }

    @Override
    public boolean logout(String token) {
        UserVo userVo = jwtUtils.parseToken(token);
        if (userVo == null || userVo.getId() == null) return false;
        return sysUserTokenService.expireToken(token);
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
        log.debug("搜索用户 关键词:{} 页码:{}", keyword, page);
        SearchUserVo result = new SearchUserVo();
        result.setPageSize(10L);
        result.setTotal(0);
        result.setRecords(List.of());
        if (keyword == null || keyword.isEmpty()) return result;

        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.like(SysUser::getUsername, keyword).or().like(SysUser::getNickname, keyword);
        IPage<SysUser> iPage = baseMapper.selectPage(new Page<>(page, result.getPageSize()), qw);

        if (iPage.getTotal() == 0) return result;

        List<StrangerVo> records = new ArrayList<>();
        iPage.getRecords().forEach(sysUser -> {
            StrangerVo record = new StrangerVo();
            BeanUtils.copyProperties(sysUser, record);
            records.add(record);
        });
        result.setRecords(records);
        result.setTotal(iPage.getTotal());
        return result;
    }

    @Override
    public boolean updatePassword(UpdatePasswordDto updatePasswordDto) {
        SysUser user = baseMapper.selectById(updatePasswordDto.getUserId());
        if (user == null) return false;
        if (!UserUtils.isValidPassword(updatePasswordDto.getNewPassword()))
            throw new CommonException(CommonExceptionEnum.PASSWORD_INVALID);
        // 判断验证码是否正确
        if (emailService.verifyCode(user.getEmail(), updatePasswordDto.getCode(), EmailActionEnum.ACTION_CHANGE_PASSWORD)) {
            log.debug("用户修改密码 用户ID:{}", updatePasswordDto.getUserId());
            user.setPassword(UserUtils.encryptPassword(updatePasswordDto.getNewPassword()));
            if (baseMapper.updateById(user) == 0) {
                return false;
            }
            // 强制过期所有token
            List<SysUserToken> userTokenList = sysUserTokenService.getUserTokenList(updatePasswordDto.getUserId());
            userTokenList.forEach(token -> sysUserTokenService.expireToken(token.getToken()));
            return true;
        } else
            throw new CommonException(CommonExceptionEnum.VERIFY_CODE_ERROR);
    }

    @Override
    public String updateAvatar(int userId, MultipartFile file) {
        if (file == null) return null;
        if (!FileUtils.isImage(file.getOriginalFilename()))
            throw new CommonException(CommonExceptionEnum.FILE_TYPE_NOT_SUPPORTED); // 文件类型不支持
        if ((file.getSize() / 1024) > userProperties.getAvatarMaxSize())
            throw new CommonException(CommonExceptionEnum.FILE_SIZE_EXCEEDED); // 头像文件过大
        UserVo userVo = UserUtils.userToVo(baseMapper.selectById(userId));
        if (userVo == null || userVo.getId() == null) return null;
        log.debug("用户修改头像，用户ID：{}", userVo.getId());
        String fileType = FileUtils.getFileType(file.getOriginalFilename());
        try {
            byte[] bytes = file.getInputStream().readAllBytes();
            String checkSum = FileUtils.getCheckSum(bytes);
            if (checkSum.isEmpty())
                throw new CommonException(CommonExceptionEnum.FILE_UPLOAD_FAILED);
            String avatarPath = "/" + userProperties.getAvatarDir() + "/" + checkSum + "." + fileType;
            if (minioService.getObject(avatarPath) != null) {
                // 文件存在,更新数据库
                baseMapper.updateAvatar(userVo.getUsername(), avatarPath);
            } else {
                // 文件不存在,上传文件
                if (minioService.upload(bytes, avatarPath)) {
                    // 上传成功
                    baseMapper.updateAvatar(userVo.getUsername(), avatarPath);
                } else
                    return null;
            }
            // 删除历史头像
            if (!userVo.getAvatar().equals("/" + userProperties.getDefaultAvatarPath() + "/" + userProperties.getDefaultAvatarName())
                    && baseMapper.getSameAvatarCount(userVo.getAvatar()) == 0) {
                // 该头像没有其他用户正在使用且非默认头像时，可以删除
                if (minioService.deleteObject(userVo.getAvatar())) {
                    log.debug("删除用户历史头像成功，文件路径：{}", userVo.getAvatar());
                } else {
                    log.error("删除用户历史头像失败，文件路径：{}", userVo.getAvatar());
                }
            }
            //更新redis中userVo缓存
            userVo.setAvatar(avatarPath);
            List<SysUserToken> userTokenList = sysUserTokenService.getUserTokenList(userVo.getId());
            for (SysUserToken userToken : userTokenList) {
                jwtUtils.updateUserVo(userToken.getToken(), userVo);
            }
            return avatarPath;
        } catch (IOException e) {
            log.error("文件上传异常", e);
            return null;
        }
    }

    @Override
    public boolean updateUserInfo(UpdateUserInfoDto updateUserInfoDto) {
        if (updateUserInfoDto == null) return false;
        if (updateUserInfoDto.getNickname() == null || updateUserInfoDto.getNickname().isEmpty()) return false;
        if (!UserUtils.isValidNickname(updateUserInfoDto.getNickname()))
            throw new CommonException(CommonExceptionEnum.NICKNAME_INVALID);
        if (updateUserInfoDto.getSex() == null) return false;
        SysUser sysUser = baseMapper.selectById(updateUserInfoDto.getUserId());
        if (sysUser == null) return false;
        if (sysUser.getNickname().equals(updateUserInfoDto.getNickname()) && sysUser.getSex() == updateUserInfoDto.getSex())
            return true;
        log.debug("用户更新个人信息 用户ID:{}", updateUserInfoDto.getUserId());
        // 更新数据
        sysUser.setNickname(updateUserInfoDto.getNickname());
        sysUser.setSex(updateUserInfoDto.getSex());
        if (baseMapper.updateById(sysUser) == 0) return false;
        // 更新redis中的userVo
        List<SysUserToken> userTokenList = sysUserTokenService.getUserTokenList(updateUserInfoDto.getUserId());
        UserVo userVo = UserUtils.userToVo(sysUser);
        for (SysUserToken userToken : userTokenList) {
            jwtUtils.updateUserVo(userToken.getToken(), userVo);
        }
        return true;
    }

    @Override
    public StrangerVo getStrangerInfo(int strangerId) {
        log.debug("获取陌生人信息 陌生人ID:{}", strangerId);
        SysUser sysUser = baseMapper.selectById(strangerId);
        return UserUtils.userToStrangerVo(sysUser);
    }

    @Override
    public boolean updateEmail(UpdateEmailDto updateEmailDto) {
        if (updateEmailDto == null) return false;
        if (!UserUtils.isValidEmail(updateEmailDto.getNewEmail()))
            throw new CommonException(CommonExceptionEnum.EMAIL_INVALID);
        SysUser sysUser = baseMapper.selectById(updateEmailDto.getUserId());
        if (sysUser == null) return false;
        // 判断新邮箱是否已注册
        if (this.isEmailExist(updateEmailDto.getNewEmail()))
            throw new CommonException(CommonExceptionEnum.EMAIL_EXIST);
        if (sysUser.getEmail().equals(updateEmailDto.getNewEmail())) return true;
        // 判断验证码是否正确
        if (emailService.verifyCode(sysUser.getEmail(), updateEmailDto.getCode(), EmailActionEnum.ACTION_CHANGE_EMAIL) &&
                emailService.verifyCode(updateEmailDto.getNewEmail(), updateEmailDto.getNewCode(), EmailActionEnum.ACTION_EMAIL_VERIFY)) {
            log.debug("用户更新邮箱 用户ID:{}", updateEmailDto.getUserId());
            sysUser.setEmail(updateEmailDto.getNewEmail());
            if (baseMapper.updateById(sysUser) == 0) return false;
            // 强制过期所有token
            List<SysUserToken> userTokenList = sysUserTokenService.getUserTokenList(updateEmailDto.getUserId());
            userTokenList.forEach(token -> sysUserTokenService.expireToken(token.getToken()));
            return true;
        } else
            throw new CommonException(CommonExceptionEnum.VERIFY_CODE_ERROR);
    }
}