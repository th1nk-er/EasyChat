package top.th1nk.easychat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.th1nk.easychat.domain.SysUserToken;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author vinka
 * @description 针对表【ec_user_token】的数据库操作Mapper
 * @createDate 2024-07-11 13:19:28
 * @Entity top.th1nk.easychat.domain.SysUserToken
 */
public interface SysUserTokenMapper extends BaseMapper<SysUserToken> {
    /**
     * 更新Token的过期时间
     *
     * @param token      token字符串
     * @param expireTime 过期时间
     * @return 更新记录数
     */
    int updateExpireTimeByToken(String token, LocalDateTime expireTime);

    /**
     * 根据用户ID查询其Token列表
     *
     * @param userId 用户ID
     * @return Token列表
     */
    List<SysUserToken> getByUserId(Integer userId);
}




