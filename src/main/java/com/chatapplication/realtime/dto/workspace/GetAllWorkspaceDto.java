package com.chatapplication.realtime.dto.workspace;

import com.chatapplication.realtime.entity.Workspace;
import com.chatapplication.realtime.entity.type.MemberRole;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllWorkspaceDto {
//    private Workspace workspaces;
    private Long workspaceId;
    private String workspaceName;
    private WorkspaceMemberStatusType Status;
    private MemberRole role;
}
