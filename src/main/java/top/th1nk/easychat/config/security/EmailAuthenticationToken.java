package top.th1nk.easychat.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class EmailAuthenticationToken extends AbstractAuthenticationToken {

    private final String email;
    private final String code;

    public EmailAuthenticationToken(String email, String code) {
        super(null);
        this.email = email;
        this.code = code;
    }

    public EmailAuthenticationToken(Collection<? extends GrantedAuthority> authorities, String email, String code) {
        super(authorities);
        this.email = email;
        this.code = code;
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }
}
