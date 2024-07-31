package top.th1nk.easychat.config.websocket;

import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import top.th1nk.easychat.config.easychat.EasyChatConfiguration;
import top.th1nk.easychat.config.easychat.WebSocketProperties;
import top.th1nk.easychat.domain.vo.UserVo;
import top.th1nk.easychat.utils.JwtUtils;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketProperties webSocket;
    @Resource
    private JwtUtils jwtUtils;

    public WebSocketConfig(EasyChatConfiguration easyChatConfiguration) {
        this.webSocket = easyChatConfiguration.getWebSocket();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker(webSocket.getTopicPrefix().toArray(String[]::new));
        // 前端发起请求时的前缀
        config.setApplicationDestinationPrefixes(webSocket.getAppDesPrefix());
        config.setUserDestinationPrefix(webSocket.getUserDesPrefix());
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocket.getEndpoint()).setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(@NotNull Message<?> message, @NotNull MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) return message;
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    List<String> authHeaderList = accessor.getNativeHeader("Authentication");
                    if (authHeaderList == null)
                        return message;
                    String tokenString = authHeaderList.getFirst();
                    UserVo userVo = jwtUtils.parseToken(tokenString);
                    if (userVo == null) return message;
                    accessor.setUser(new UsernamePasswordAuthenticationToken(userVo.getUsername(), tokenString));
                }
                return message;
            }
        });
    }
}