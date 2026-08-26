package com.chatapplication.realtime.service;


import com.chatapplication.realtime.dto.UserResponseDto;
import com.chatapplication.realtime.dto.channel.ChannelResponseDto;
import com.chatapplication.realtime.dto.workspace.*;
import com.chatapplication.realtime.entity.*;
import com.chatapplication.realtime.entity.type.ChannelType;
import com.chatapplication.realtime.entity.type.MemberRole;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import com.chatapplication.realtime.repository.*;
import lombok.RequiredArgsConstructor;
import org.hibernate.jdbc.Work;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;


    public GetAllWorkspaceDto addWorkspace(WorkspaceRequestDto requestDto, User user){


         Workspace workspace = Workspace.builder()
                 .workspace_name(requestDto.getName())
                 .createdBy(user)
                 .build();

         Workspace savedWorkspace = workspaceRepository.save(workspace);

         WorkspaceMember workspaceMember = WorkspaceMember.builder()
                 .workspace(savedWorkspace)
                 .user(user)
                 .role(MemberRole.OWNER)
                 .status(WorkspaceMemberStatusType.ACTIVE)
                 .build();
         workspaceMemberRepository.save(workspaceMember);

         Channel channel = Channel.builder()
                 .workspace(workspace)
                 .name("general")
                 .type(ChannelType.PUBLIC)
                 .createdBy(user)
                 .build();
         channelRepository.save(channel);
         return new GetAllWorkspaceDto(workspace.getWorkspace_id(),workspace.getWorkspace_name(),workspaceMember.getStatus(),workspaceMember.getRole());



    }

    public WorkspaceMemberResponseDto createWorkspaceMember(WorkspaceMemberRequestDto requestDto,Long workspaceId){
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(()-> new RuntimeException("User does not exist"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()-> new RuntimeException("workspace does not exist"));

        Optional<WorkspaceMember> existingMember =
                workspaceMemberRepository.findByWorkspaceAndUser(workspace, user);

        if(existingMember.isPresent()){
            WorkspaceMember member = existingMember.get();
            if(member.getStatus() != WorkspaceMemberStatusType.REMOVED){
                throw new RuntimeException("User is already a member");
            }
            member.setStatus(WorkspaceMemberStatusType.INVITED);
            member.setRole(MemberRole.MEMBER);

        }

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .user(user)
                .workspace(workspace)
                .role(MemberRole.MEMBER)
                .status(WorkspaceMemberStatusType.INVITED)
                .build();
        workspaceMemberRepository.save(workspaceMember);
        return new WorkspaceMemberResponseDto(workspaceMember.getId(),workspaceMember.getUser().getId(), workspaceMember.getUser().getUsername(),workspaceMember.getUser().getEmail(),workspaceMember.getRole(),workspaceMember.getStatus());
    }

    public WorkspaceMemRemDto removeMember(Long workspaceId ,WorkspaceMemberRequestDto requestDto){
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(()-> new RuntimeException("User does not exist"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()-> new RuntimeException("workspace does not exist"));

        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceAndUser(workspace,user).orElseThrow(()->new RuntimeException("Member not found"));

        member.setStatus(WorkspaceMemberStatusType.REMOVED);
        workspaceMemberRepository.save(member);
        return new WorkspaceMemRemDto("this member is removed",member.getUser().getEmail());

    }

    public List<GetAllWorkspaceDto> getAllWorkspaces(User user){
         List<WorkspaceMember> workspaceMembers = workspaceMemberRepository.findByUserAndStatusNotIn(
                 user,
                 List.of(
                         WorkspaceMemberStatusType.REMOVED,
                         WorkspaceMemberStatusType.LEFT
                 )
                );

        if (workspaceMembers.isEmpty()) {
            throw new RuntimeException("No workspaces found.");
        }

        return workspaceMembers.stream()
                .map(member->{
                    Workspace workspace = member.getWorkspace();
                    return new GetAllWorkspaceDto(
                            workspace.getWorkspace_id(),
                            workspace.getWorkspace_name(),
                            member.getStatus(),
                            member.getRole()
                    );
                })
                .toList();
    }

    public WorkspaceMemAndChannelDto getMembers(Long workspaceId,User user){

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()->
                new RuntimeException("workspace does not exist"));


         WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceAndUser(workspace,user).orElseThrow(() ->
                 new RuntimeException("Access Denied."));

        // Get members
        List<WorkspaceMemberResponseDto> members = workspaceMemberRepository
                .findByWorkspace(workspace)
                .stream()
                .map(member -> new WorkspaceMemberResponseDto(
                        member.getId(),
                        member.getUser().getId(),
                        member.getUser().getUsername(),
                        member.getUser().getEmail(),
                        member.getRole(),
                        member.getStatus()
                ))
                .toList();

        // Get channels
        List<ChannelResponseDto> channels = channelRepository
                .findByWorkspace(workspace)
                .stream()
                .map(channel -> new ChannelResponseDto(
                        channel.getChannelId(),
                        channel.getName(),
                        channel.getType()

                ))
                .toList();

        return new WorkspaceMemAndChannelDto(workspace.getWorkspace_id(),members,channels);

    }

    public UserResponseDto deleteWorkspaceMember(Long memberId,User user){
        WorkspaceMember member1 = workspaceMemberRepository.findById(memberId).orElseThrow(()-> new RuntimeException(
                "Workspace member does not exist"));
        WorkspaceMember member2 = workspaceMemberRepository.findByWorkspaceAndUser(member1.getWorkspace(),user).orElseThrow(()->new RuntimeException(
                "you are not a member of this workspace"
        ));
        if(member2.getRole().equals(MemberRole.MEMBER)){
            throw new IllegalArgumentException("you are not allowed to delete any members of this workspace Only admin or owner can do");
        }
        WorkspaceInvitation invitedMember = workspaceInvitationRepository.findByInvitedUserAndWorkspace(member1.getUser(),member1.getWorkspace());

        workspaceMemberRepository.delete(member1);
        workspaceInvitationRepository.delete(invitedMember);

        return new UserResponseDto(member1.getUser().getEmail(),member1.getUser().getId(),member1.getUser().getUsername());
    }


}
