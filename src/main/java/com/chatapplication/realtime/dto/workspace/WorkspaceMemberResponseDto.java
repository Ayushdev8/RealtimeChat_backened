package com.chatapplication.realtime.dto.workspace;


import com.chatapplication.realtime.entity.type.MemberRole;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemberResponseDto {

    private Long id;
    private Long userId;
    private String name ;
    private String email;
    private MemberRole role;
    private WorkspaceMemberStatusType status;
}
