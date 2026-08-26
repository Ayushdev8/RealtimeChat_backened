package com.chatapplication.realtime.controller;

import com.chatapplication.realtime.dto.UserResponseDto;
import com.chatapplication.realtime.dto.websocketDto.WorkspaceInvitationDto;
import com.chatapplication.realtime.dto.workspace.*;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.WorkspaceInvitation;
import com.chatapplication.realtime.entity.WorkspaceMember;
import com.chatapplication.realtime.service.WorkspaceInvitationService;
import com.chatapplication.realtime.service.WorkspaceService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final WorkspaceInvitationService workspaceInvitationService;

    @PostMapping("/create")
    public ResponseEntity<GetAllWorkspaceDto> workspace(@RequestBody WorkspaceRequestDto requestDto, Authentication authentication){
        System.out.println("workspace controller");
        User user =(User) authentication.getPrincipal();
        GetAllWorkspaceDto responseDto = workspaceService.addWorkspace(requestDto,user);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/member/{workbenchId}")
    public ResponseEntity<WorkspaceMemberResponseDto> workspaceMember(@PathVariable Long workbenchId , @RequestBody WorkspaceMemberRequestDto requestDto ){
        System.out.println("workspacemember controller");
        WorkspaceMemberResponseDto responseDto = workspaceService.createWorkspaceMember(requestDto,workbenchId);

        return ResponseEntity.ok(responseDto);

    }

    @PatchMapping("/member/remove/{workspaceId}")
    public ResponseEntity<WorkspaceMemRemDto> removeMember(@PathVariable Long workspaceId, @RequestBody WorkspaceMemberRequestDto requestDto){
        System.out.println("workspacemember remove controller");

        WorkspaceMemRemDto responseDto = workspaceService.removeMember(workspaceId,requestDto);

        return ResponseEntity.ok(responseDto);


    }

    @GetMapping("/getall")
    public ResponseEntity<List<GetAllWorkspaceDto>> getAllWorkspace(Authentication authentication){
        User user = (User) authentication.getPrincipal();

        List<GetAllWorkspaceDto> workspaceDtos = workspaceService.getAllWorkspaces(user);

        return ResponseEntity.ok(workspaceDtos);

    }

    @GetMapping("/getMembersAndChannels/{workspaceId}")
    public ResponseEntity<WorkspaceMemAndChannelDto> getMembers(@PathVariable Long workspaceId,Authentication authentication){
        System.out.println("workspacemember controller");
        User user = (User) authentication.getPrincipal();
        WorkspaceMemAndChannelDto responseDto = workspaceService.getMembers(workspaceId,user);
        return ResponseEntity.ok(responseDto);

    }

    @DeleteMapping("/deleteMember/{memberId}")
    public ResponseEntity<UserResponseDto> deleteWorkspaceMember(@PathVariable Long memberId, Authentication authentication){
        User user = (User) authentication.getPrincipal();
        UserResponseDto response = workspaceService.deleteWorkspaceMember(memberId,user);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/invitation/{workspaceId}")
    public ResponseEntity<WorkspaceInvitationDto> addWorkspaceInvitation(@PathVariable Long workspaceId,
                                                                      @RequestBody WorkspaceMemberRequestDto requestDto,Authentication authentication){
        User user =(User) authentication.getPrincipal();
        WorkspaceInvitationDto workspaceInvitation = workspaceInvitationService.createInvitation(workspaceId,requestDto,user);
        return ResponseEntity.ok(workspaceInvitation);
    }
    @GetMapping("/getInvitation")
    public ResponseEntity<List<WorkspaceInvitationDto>> getInvitation(Authentication authentication){
        User user =(User) authentication.getPrincipal();
        List<WorkspaceInvitationDto> invitation = workspaceInvitationService.getPendingInvitations(user);
        return ResponseEntity.ok(invitation);
    }
    @PutMapping("/invitation/{invitationId}/accept")
     public ResponseEntity<WorkspaceInvitationDto> acceptInvitation(@PathVariable Long invitationId,Authentication authentication){
        User user =(User) authentication.getPrincipal();
        WorkspaceInvitationDto workspaceInvitation = workspaceInvitationService.respondToInvitation(invitationId,user,true);
        return ResponseEntity.ok(workspaceInvitation);
    }

    @PutMapping("/invitation/{invitationId}/reject")
    public ResponseEntity<WorkspaceInvitationDto> rejectInvitation(@PathVariable Long invitationId,Authentication authentication){
        User user =(User) authentication.getPrincipal();
        WorkspaceInvitationDto workspaceInvitation = workspaceInvitationService.respondToInvitation(invitationId,user,false);
        return ResponseEntity.ok(workspaceInvitation);
    }


}
