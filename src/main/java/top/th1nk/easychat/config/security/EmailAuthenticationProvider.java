package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.domain.SysUser;
import top.th1nk.easychat.enums.EmailActionEnum;
import top.th1nk.easychat.exception.enums.LoginExceptionEnum;
import top.th1nk.easychat.service.EmailService;

@Component
@Data
public class EmailAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;
    @Resource
    private EmailService emailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmailAuthenticationToken token = (EmailAuthenticationToken) authentication;
        String email = (String) token.getPrincipal();
        String code = (String) token.getCredentials();
        SysUser user = (SysUser) userDetailsService.loadUserByUsername(email);
        if (!emailService.verifyCode(email, code, EmailActionEnum.ACTION_LOGIN)) {
            throw new BadCredentialsException(LoginExceptionEnum.EMAIL_VERIFY_CODE_ERROR.getMessage());
        }
        return new EmailAuthenticationToken(email, code, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
