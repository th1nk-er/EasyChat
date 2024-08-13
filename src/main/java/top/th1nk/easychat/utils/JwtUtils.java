package top.th1nk.easychat.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.Nullable;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.config.easychat.JwtProperties;
import top.th1nk.easychat.domain.SysUserToken;
import top.th1nk.easychat.domain.vo.UserVo;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * jwt工具类
 */
@Slf4j
@Component
public class JwtUtils {
    private static final String TOKEN_PREFIX = "user:token:";
    @Resource
    private JwtProperties jwtProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 生成token
     *
     * @param userVo 用户信息
     * @return UserToken实体类
     */
    public SysUserToken generateToken(UserVo userVo) {
        SysUserToken userToken = new SysUserToken();
        LocalDateTime now = LocalDateTime.now();
        userToken.setUserId(userVo.getId());
        userToken.setIssueTime(now);
        userToken.setExpireTime(now.plusSeconds(jwtProperties.getExpireSeconds()));
        userToken.setLoginIp(RequestUtils.getClientIp());
        userToken.setUserAgent(RequestUtils.getUserAgent());
        userToken.setToken(Jwts.builder()
                .subject(userVo.getUsername())
                .issuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
                .expiration(Date.from((userToken.getExpireTime().atZone(ZoneId.systemDefault())).toInstant()))
                .signWith(this.getKey())
                .compact());
        log.info("用户创建Token 用户名:{}", userVo.getUsername());
        saveToken(userToken, userVo);
        return userToken;
    }

    /**
     * 解析token
     *
     * @param tokenString token字符串
     * @return UserVo实体类 解析失败返回null
     */
    @Nullable
    public UserVo parseToken(@Nullable String tokenString) {
        if (tokenString == null || tokenString.isEmpty() || tokenString.isBlank())
            return null;
        UserVo userVo;
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) this.getKey())
                    .build()
                    .parse(tokenString);
            userVo = (UserVo) redisTemplate.opsForValue().get(TOKEN_PREFIX + tokenString);
        } catch (SecurityException e) {
            // 签名错误
            return null;
        } catch (MalformedJwtException e) {
            // 无效Token
            return null;
        } catch (ExpiredJwtException e) {
            // Token过期
            return null;
        } catch (UnsupportedJwtException e) {
            // 不支持的token
            return null;
        }
        return userVo;
    }


    /**
     * 强制过期token
     *
     * @param token token字符串
     */
    public void expireToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    private Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtProperties.getSecret()));
    }

    /**
     * 刷新token对应的userVo
     *
     * @param tokenString token字符串
     * @param userVo      userVo
     */
    public void updateUserVo(String tokenString, UserVo userVo) {
        if (tokenString == null || tokenString.isEmpty() || userVo == null) return;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + tokenString))) {
            // 当原key存在时，更新userVo
            Long expire = redisTemplate.getExpire(TOKEN_PREFIX + tokenString);
            if (expire != null)
                redisTemplate.opsForValue().set(TOKEN_PREFIX + tokenString, userVo, Duration.ofSeconds(expire));
        }
    }

    private void saveToken(SysUserToken userToken, UserVo userVo) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userToken.getToken(), userVo, Duration.ofSeconds(jwtProperties.getExpireSeconds()));
    }
}
