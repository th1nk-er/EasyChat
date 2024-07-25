package top.th1nk.easychat.config.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import top.th1nk.easychat.config.easychat.EasyChatConfiguration;
import top.th1nk.easychat.config.easychat.WebSocketProperties;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketProperties webSocket;

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
        registry.addEndpoint(webSocket.getEndpoint()).setAllowedOrigins("*");
    }
}