package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.JwtProperties;
import top.th1nk.easychat.domain.SysUserToken;
import top.th1nk.easychat.domain.vo.UserTokenVo;
import top.th1nk.easychat.mapper.SysUserTokenMapper;
import top.th1nk.easychat.service.SysUserTokenService;
import top.th1nk.easychat.utils.JwtUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_token】的数据库操作Service实现
 * @createDate 2024-07-11 13:19:28
 */
@Slf4j
@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenMapper, SysUserToken>
        implements SysUserTokenService {

    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private JwtUtils jwtUtils;

    @Override
    public void expireToken(String token) {
        if (token == null || token.isEmpty()) return;
        log.debug("强制过期token: {}", token);
        if (baseMapper.updateExpireTimeByToken(token, LocalDateTime.now()) == 1)
            jwtUtils.expireToken(token);
    }

    @Override
    public void saveUserToken(SysUserToken sysUserToken) {
        log.debug("保存token到数据库: {}", sysUserToken);
        int max = jwtProperties.getMaxToken();
        List<SysUserToken> userTokenList = baseMapper.getByUserId(sysUserToken.getUserId());
        if (userTokenList.isEmpty() || userTokenList.size() < max)
            baseMapper.insert(sysUserToken);
        else {
            // 根据token的issueTime排序，获取最早创建的token
            userTokenList.sort(Comparator.comparing(SysUserToken::getIssueTime));
            sysUserToken.setId(userTokenList.getFirst().getId());
            // 覆盖最早的token
            jwtUtils.expireToken(userTokenList.getFirst().getToken());
            baseMapper.updateById(sysUserToken);
        }
    }

    @Override
    public List<SysUserToken> getUserTokenList(Integer userId) {
        log.debug("获取用户token列表 userId: {}", userId);
        LambdaQueryWrapper<SysUserToken> qw = new LambdaQueryWrapper<>();
        qw.eq(SysUserToken::getUserId, userId);
        return baseMapper.selectList(qw);
    }

    @Override
    public List<UserTokenVo> getUserTokenVoList(int userId) {
        List<SysUserToken> userTokenList = getUserTokenList(userId);
        if (userTokenList.isEmpty()) return List.of();
        return userTokenList.stream().map(token -> {
            UserTokenVo vo = new UserTokenVo();
            BeanUtils.copyProperties(token, vo);
            return vo;
        }).toList();
    }
}




