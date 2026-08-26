package com.chatapplication.realtime.websocket;

import com.chatapplication.realtime.dto.websocketDto.DisconnectedUser;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PresenceService {

    private final Map<Long, Set<String>> userSessions = new ConcurrentHashMap<>();

    private final Map<String, Long> sessionUsers = new ConcurrentHashMap<>();

    // userId -> workspaceId they are currently viewing (nullable / absent if none)
    private final Map<Long, Long> userActiveWorkspace = new ConcurrentHashMap<>();
    // sessionId -> workspaceId (so disconnect knows which workspace to clear/broadcast)
    private final Map<String, Long> sessionWorkspace = new ConcurrentHashMap<>();

    public void userConnected(Long userId, String sessionId) {

        userSessions
                .computeIfAbsent(userId, key -> ConcurrentHashMap.newKeySet())
                .add(sessionId);

        sessionUsers.put(sessionId, userId);
    }

    /**
     * Called when a user enters (or switches into) a workspace.
     * Returns the previous workspaceId for this session, if any, so the caller
     * can broadcast an OFFLINE event to that workspace's topic.
     */
    public Long userEnteredWorkspace(Long userId, String sessionId, Long workspaceId) {
        Long previousWorkspace = sessionWorkspace.put(sessionId, workspaceId);
        userActiveWorkspace.put(userId, workspaceId);
        return previousWorkspace;
    }

    /** Called when a user explicitly leaves a workspace (e.g. navigates away) without disconnecting. */
    public void userLeftWorkspace(String sessionId) {
        sessionWorkspace.remove(sessionId);
    }

    public DisconnectedUser userDisconnected(String sessionId) {

        Long userId = sessionUsers.remove(sessionId);
        Long workspaceId = sessionWorkspace.remove(sessionId);

        if (userId == null) {
            return new DisconnectedUser(null, false,workspaceId);
        }

        Set<String> sessions = userSessions.get(userId);

        if (sessions == null) {
            return new DisconnectedUser(userId, true,workspaceId);
        }

        sessions.remove(sessionId);

        if (sessions.isEmpty()) {
            userSessions.remove(userId);
            userActiveWorkspace.remove(userId);

            return new DisconnectedUser(userId, true,workspaceId);
        }

        return new DisconnectedUser(userId, false,workspaceId);
    }

    public boolean isOnline(Long userId) {

        Set<String> sessions = userSessions.get(userId);

        return sessions != null && !sessions.isEmpty();
    }

    /** Online users currently active specifically in this workspace. */
    public Set<Long> getOnlineUsersInWorkspace(Long workspaceId) {
        return userActiveWorkspace.entrySet().stream()
                .filter(e -> e.getValue().equals(workspaceId))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }
}
