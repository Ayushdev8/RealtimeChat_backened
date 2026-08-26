package com.chatapplication.realtime.dto.websocketDto;



import lombok.Data;

import java.util.Set;

@Data
public class InitialPresenceEvent {
    private final String type = "INITIAL_PRESENCE";
    private final Set<Long> onlineUsers;

    public InitialPresenceEvent(Set<Long> onlineUsers) {
        this.onlineUsers = onlineUsers;
    }
}
