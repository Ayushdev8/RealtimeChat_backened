package com.chatapplication.realtime.websocket;

import com.chatapplication.realtime.dto.websocketDto.DisconnectedUser;
import com.chatapplication.realtime.dto.websocketDto.InitialPresenceEvent;
import com.chatapplication.realtime.dto.websocketDto.PresenceEvent;
import com.chatapplication.realtime.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class PresenceEventListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleConnect(SessionConnectedEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Authentication authentication =
                (Authentication) accessor.getUser();

        if (authentication == null) {
            System.out.println("No authentication found");
            return;
        }
        User user = (User) authentication.getPrincipal();


        Long userId = user.getId();

        String sessionId = accessor.getSessionId();
        System.out.println("Username: " + user.getUsername());
        System.out.println(authentication.getName());

        System.out.println("Session: " + sessionId);

        presenceService.userConnected(userId, sessionId);




        // Tell everyone else that this user is online
//        messagingTemplate.convertAndSend(
//                "/topic/presence",
//                new PresenceEvent(
//                        "USER_ONLINE",
//                        userId
//                )
//        );
    }


    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        DisconnectedUser result =
                presenceService.userDisconnected(
                        event.getSessionId()
                );

        if (!result.isWentOffline() || result.getUserId() == null || result.getWorkspaceId() == null) {
            return;
        }


        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceEvent(
                        "USER_OFFLINE",
                        result.getUserId()
                )
        );
    }
}
