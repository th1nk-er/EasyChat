package top.th1nk.easychat.config.websocket;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import top.th1nk.easychat.service.WebSocketService;

@Component
@Slf4j
public class WebSocketEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Resource
    private WebSocketService webSocketService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Authentication auth = (Authentication) headerAccessor.getUser();
        if (auth != null) {
            webSocketService.handleUserDisconnected(auth);
        }
    }
}