package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.EasyChatConfiguration;
import top.th1nk.easychat.domain.SysUserToken;
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
@Service
public class SysUserTokenServiceImpl extends ServiceImpl<SysUserTokenMapper, SysUserToken>
        implements SysUserTokenService {

    @Resource
    private EasyChatConfiguration easyChatConfiguration;
    @Resource
    private JwtUtils jwtUtils;

    @Override
    public void expireToken(String token) {
        if (token == null || token.isEmpty()) return;
        baseMapper.updateExpireTimeByToken(token, LocalDateTime.now());
    }

    @Override
    public void saveUserToken(SysUserToken sysUserToken) {
        int max = easyChatConfiguration.getJwt().getMaxToken();
        List<SysUserToken> userTokenList = baseMapper.getByUserId(sysUserToken.getUserId());
        if (userTokenList.isEmpty() || userTokenList.size() < max)
            baseMapper.insert(sysUserToken);
        else {
            // 根据token的issueTime排序，获取最早创建的token
            userTokenList.sort(Comparator.comparing(SysUserToken::getIssueTime));
            sysUserToken.setId(userTokenList.get(0).getId());
            // 覆盖最早的token
            jwtUtils.expireToken(userTokenList.get(0).getToken());
            baseMapper.updateById(sysUserToken);
        }
    }
}




