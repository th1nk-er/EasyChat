package top.th1nk.easychat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.th1nk.easychat.domain.SysUserToken;

import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_token】的数据库操作Service
 * @createDate 2024-07-11 13:19:28
 */
public interface SysUserTokenService extends IService<SysUserToken> {
    /**
     * 强制过期token
     *
     * @param token token字符串
     */
    void expireToken(String token);

    /**
     * 保存token到数据库
     * 并检查是否超出token在线数，超出则强制覆盖最早token
     *
     * @param sysUserToken token
     */
    void saveUserToken(SysUserToken sysUserToken);

    /**
     * 获取用户token列表
     *
     * @param userId 用户id
     * @return token列表
     */
    List<SysUserToken> getUserTokenList(Integer userId);
}
