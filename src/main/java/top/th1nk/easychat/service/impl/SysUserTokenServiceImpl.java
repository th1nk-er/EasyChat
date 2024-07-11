package top.th1nk.easychat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.th1nk.easychat.config.easychat.EasyChatConfiguration;
import top.th1nk.easychat.domain.SysUserToken;
import top.th1nk.easychat.mapper.SysUserTokenMapper;
import top.th1nk.easychat.service.SysUserTokenService;

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
            // 检查是否有过期token，如果有过期token，则覆盖，否则删除最早创建的token
            // 根据token的issueTime排序，获取最早创建的token
            userTokenList.sort(Comparator.comparing(SysUserToken::getIssueTime));
            SysUserToken updateEntity = userTokenList.get(0);
            updateEntity.setToken(sysUserToken.getToken());
            updateEntity.setIssueTime(sysUserToken.getIssueTime());
            updateEntity.setExpireTime(sysUserToken.getExpireTime());
            baseMapper.updateById(updateEntity);
        }
    }
}




