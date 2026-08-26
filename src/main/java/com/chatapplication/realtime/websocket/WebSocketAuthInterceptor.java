package com.chatapplication.realtime.websocket;

import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.repository.UserRepository;
import com.chatapplication.realtime.security.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel
    ) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );

        if (accessor != null &&
                StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader =
                    accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null ||
                    !authHeader.startsWith("Bearer ")) {

                throw new IllegalArgumentException(
                        "Missing Authorization header"
                );
            }

            String token = authHeader.substring(7);

            String email = authUtil.getEmailFromToken(token);

            User user = userRepository
                    .findByEmail(email)
                    .orElseThrow(() ->
                            new RuntimeException("User not found")
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            user.getAuthorities()
                    );

            accessor.setUser(authentication);

            System.out.println("================================");
            System.out.println("🔥 WebSocket CONNECT");
            System.out.println("🔥 Email: " + email);
            System.out.println(
                    "🔥 Principal: " +
                            authentication.getName()
            );
            System.out.println(
                    "🔥 User ID: " +
                            user.getId()
            );
            System.out.println("================================");

        }

        return message;
    }

}
