package top.th1nk.easychat.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
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
    @Value("${easy-chat.jwt.secret}")
    private String secret;
    @Value("${easy-chat.jwt.expireSeconds:3600}")
    private long expireSeconds;
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
        userToken.setExpireTime(now.plusSeconds(expireSeconds));
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
    public UserVo parseToken(String tokenString) {
        if (tokenString == null || tokenString.isEmpty() || tokenString.isBlank())
            return null;
        UserVo userVo;
        try {
            Claims claims = (Claims) Jwts.parser()
                    .verifyWith((SecretKey) this.getKey())
                    .build()
                    .parse(tokenString)
                    .getPayload();
            userVo = (UserVo) redisTemplate.opsForValue().get(TOKEN_PREFIX + tokenString);
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
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secret));
    }

    private void saveToken(SysUserToken userToken, UserVo userVo) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + userToken.getToken(), userVo, Duration.ofSeconds(expireSeconds));
    }

}
