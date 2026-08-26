package com.chatapplication.realtime.dto.websocketDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DisconnectedUser {
    private Long userId;
    private boolean wentOffline;
    private Long workspaceId;

}
