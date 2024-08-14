package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import top.th1nk.easychat.enums.EmailActionEnum;
import top.th1nk.easychat.enums.LoginExceptionEnum;
import top.th1nk.easychat.service.EmailService;

@Component
public class EmailAuthenticationProvider implements AuthenticationProvider {
    @Resource
    private EmailService emailService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        EmailAuthenticationToken token = (EmailAuthenticationToken) authentication;
        String email = (String) token.getPrincipal();
        String code = (String) token.getCredentials();
        if (!emailService.verifyCode(email, code, EmailActionEnum.ACTION_LOGIN)) {
            throw new BadCredentialsException(LoginExceptionEnum.EMAIL_VERIFY_CODE_ERROR.getMessage());
        }
        token.setAuthenticated(true);
        return token;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return EmailAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
