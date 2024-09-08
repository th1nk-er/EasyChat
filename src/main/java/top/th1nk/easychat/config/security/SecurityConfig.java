package top.th1nk.easychat.config.security;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import top.th1nk.easychat.config.easychat.SecurityProperties;

import java.util.List;

/*
 配置Spring Security
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Resource(name = "passwordUserDetailsService")
    private UserDetailsService passwordUserDetailsService;
    @Resource(name = "emailUserDetailsService")
    private UserDetailsService emailUserDetailsService;
    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    @Resource
    private EmailAuthenticationProvider emailAuthenticationProvider;

    @Resource
    private SecurityProperties securityProperties;

    @Bean
    public AuthenticationManager authenticationManager(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(passwordUserDetailsService);
        emailAuthenticationProvider.setUserDetailsService(emailUserDetailsService);
        return new ProviderManager(provider, emailAuthenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        List<String> permitAllUrls = securityProperties.getPermitAllUrls();
        List<String> authorizeUrls = securityProperties.getAuthorizeUrls();
        String[] perms;
        String[] auths;
        if (permitAllUrls != null)
            perms = permitAllUrls.toArray(new String[0]);
        else perms = new String[0];
        if (authorizeUrls != null)
            auths = authorizeUrls.toArray(new String[0]);
        else auths = new String[0];
        http
                .csrf(AbstractHttpConfigurer::disable) // 关闭CSRF
                .cors(config -> {
                    CorsConfiguration corsConfiguration = new CorsConfiguration();
                    corsConfiguration.addAllowedMethod("*");
                    corsConfiguration.addAllowedHeader("*");
                    corsConfiguration.addAllowedOriginPattern("*");
                    corsConfiguration.setAllowCredentials(true);

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", corsConfiguration);
                    config.configurationSource(source);

                }) // 配置跨域
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(perms).permitAll()
                        .requestMatchers(auths).authenticated()
                        .anyRequest().authenticated());

        // JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
