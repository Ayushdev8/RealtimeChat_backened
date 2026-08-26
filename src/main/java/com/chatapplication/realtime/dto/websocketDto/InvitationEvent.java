package com.chatapplication.realtime.dto.websocketDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvitationEvent {
    private String type; // "INVITE_RECEIVED" | "INVITE_ACCEPTED" | "INVITE_REJECTED"
    private Long invitationId;
    private Long workspaceId;
    private String workspaceName;
    private String invitedByName;
}
