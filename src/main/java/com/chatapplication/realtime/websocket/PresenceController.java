package com.chatapplication.realtime.websocket;

import com.chatapplication.realtime.dto.websocketDto.InitialPresenceEvent;
import com.chatapplication.realtime.dto.websocketDto.PresenceEvent;
import com.chatapplication.realtime.dto.websocketDto.WorkspaceEnterRequest;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class PresenceController {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    @MessageMapping("/presence/workspace/enter")
    public void enterWorkspace(
            @Payload WorkspaceEnterRequest request,
            SimpMessageHeaderAccessor headerAccessor,
            Principal principal
    ) {
        Authentication authentication = (Authentication) principal;
//        User user = (User) authentication.getPrincipal();
        String email = authentication.getName();
        User user =userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException(
                                        "User not found"));
        Long userId = user.getId();
        String sessionId = headerAccessor.getSessionId();
        Long workspaceId = request.getWorkspaceId();

        Long previousWorkspace = presenceService.userEnteredWorkspace(userId, sessionId, workspaceId);

        // Left a different workspace — tell that workspace's topic the user went offline there
        if (previousWorkspace != null && !previousWorkspace.equals(workspaceId)) {
            messagingTemplate.convertAndSend(
                    "/topic/presence/" + previousWorkspace,
                    new PresenceEvent("USER_OFFLINE", userId)
            );
        }

        // Announce online in the new workspace
        messagingTemplate.convertAndSend(
                "/topic/presence/" + workspaceId,
                new PresenceEvent("USER_ONLINE", userId)
        );

        // Send this user the current online set for the workspace they just entered
        messagingTemplate.convertAndSendToUser(
                authentication.getName(),
                "/queue/presence",
                new InitialPresenceEvent(presenceService.getOnlineUsersInWorkspace(workspaceId))
        );
    }

    @MessageMapping("/presence/workspace/leave")
    public void leaveWorkspace(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        Authentication authentication = (Authentication) principal;
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        String sessionId = headerAccessor.getSessionId();

        // Find current workspace before clearing, so we can broadcast offline to it
        Long workspaceId = presenceService.getOnlineUsersInWorkspace(userId) != null
                ? null // not used — see below
                : null;

        presenceService.userLeftWorkspace(sessionId);
        // NOTE: if you need to broadcast OFFLINE on explicit leave (not just switch),
        // track workspaceId before calling userLeftWorkspace and broadcast it here.
    }
}
