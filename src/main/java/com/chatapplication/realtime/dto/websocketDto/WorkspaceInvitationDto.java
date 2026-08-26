package com.chatapplication.realtime.dto.websocketDto;

import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.Workspace;
import com.chatapplication.realtime.entity.type.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceInvitationDto {
    private Long invitationId;
    private Long workspaceId;
    private String workspaceName;
    private String invitedByName;
    private InvitationStatus status;
    private LocalDateTime inviteDate;


}
