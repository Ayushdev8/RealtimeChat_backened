package com.chatapplication.realtime.dto.workspace;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@AllArgsConstructor
@NoArgsConstructor
public class WorkspaceResponseDto {
    private String name;
    private Long createdBy;

}
