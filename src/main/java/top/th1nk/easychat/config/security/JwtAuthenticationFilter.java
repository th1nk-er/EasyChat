package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.utils.JwtUtils;
import top.th1nk.easychat.utils.SecurityUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private SecurityUtils securityUtils;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String tokenString = request.getHeader("Authorization");
        UserVo userVo = jwtUtils.parseToken(tokenString);
        if (userVo == null) {
            filterChain.doFilter(request, response);
            return;
        }
        Set<String> permissions = securityUtils.getPermissions(userVo.getId());
        Collection<SimpleGrantedAuthority> perms = null;
        if (permissions != null && !permissions.isEmpty())
            perms = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userVo.getUsername(), null, perms);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
