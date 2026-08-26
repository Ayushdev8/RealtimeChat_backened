package com.chatapplication.realtime.dto.workspace;


import com.chatapplication.realtime.dto.channel.ChannelResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceMemAndChannelDto {
    private Long workspaceId;
    private List<WorkspaceMemberResponseDto> members;
    private List<ChannelResponseDto> channels;
}
