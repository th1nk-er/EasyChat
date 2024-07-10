package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.domain.dto.RegisterDto;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.enums.CommonExceptionEnum;
import top.th1nk.easychat.enums.RegisterExceptionEnum;
import top.th1nk.easychat.exception.CommonException;
import top.th1nk.easychat.exception.RegisterException;
import top.th1nk.easychat.mapper.SysUserMapper;
import top.th1nk.easychat.service.EmailService;
import top.th1nk.easychat.service.SysUserService;
import top.th1nk.easychat.utils.UserUtils;

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

    @Override
    public SysUser getUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUser::getUsername, username);
        return baseMapper.selectOne(qw);
    }

    @Override
    public SysUser getUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUser::getEmail, email);
        return baseMapper.selectOne(qw);
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
        if (this.getUserByUsername(registerDto.getUsername()) != null)
            throw new RegisterException(RegisterExceptionEnum.USERNAME_EXIST); // 用户名已存在
        if (this.getUserByEmail(registerDto.getEmail()) != null)
            throw new RegisterException(RegisterExceptionEnum.EMAIL_EXIST); // 邮箱已存在
        if (!emailService.verifyCode(registerDto.getEmail(), registerDto.getVerifyCode()))
            throw new CommonException(CommonExceptionEnum.EMAIL_VERIFY_CODE_ERROR); // 验证码错误
        // 创建用户
        SysUser user = new SysUser();
        BeanUtils.copyProperties(registerDto, user);
        user.setPassword(UserUtils.encryptPassword(registerDto.getPassword()));
        user.setNickname(registerDto.getUsername());
//        user.setAvatar(""); // TODO 设置默认头像地址
        log.info("注册用户:{}", user);
        // 插入到数据库
        if (baseMapper.insert(user) != 1)
            throw new RegisterException(RegisterExceptionEnum.REGISTER_FAILED); // 注册失败
        // 返回用户Vo
        return UserUtils.userToVo(this.getUserByUsername(registerDto.getUsername()));
    }

}