package com.chatapplication.realtime.service;

import com.chatapplication.realtime.dto.websocketDto.InvitationEvent;
import com.chatapplication.realtime.dto.websocketDto.WorkspaceInvitationDto;
import com.chatapplication.realtime.dto.workspace.WorkspaceMemberRequestDto;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.Workspace;
import com.chatapplication.realtime.entity.WorkspaceInvitation;
import com.chatapplication.realtime.entity.WorkspaceMember;
import com.chatapplication.realtime.entity.type.InvitationStatus;
import com.chatapplication.realtime.entity.type.MemberRole;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import com.chatapplication.realtime.repository.UserRepository;
import com.chatapplication.realtime.repository.WorkspaceInvitationRepository;
import com.chatapplication.realtime.repository.WorkspaceMemberRepository;
import com.chatapplication.realtime.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceInvitationService {
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceInvitationRepository workspaceInvitationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WorkspaceMemberRepository workspaceMemberRepository;


    public WorkspaceInvitationDto createInvitation(Long workspaceId, WorkspaceMemberRequestDto request, User invitedByUser) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        User invitedUser = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceAndUser(workspace,invitedByUser).
                orElseThrow(()->new RuntimeException("Member not found"));

        if(member.getRole() != MemberRole.OWNER){
            throw new RuntimeException("you are not allowed to invite this member");

        }
        Optional<WorkspaceInvitation> existingInvitation = workspaceInvitationRepository.findByInvitedUserAndWorkspaceAndStatus(invitedUser,workspace,InvitationStatus.PENDING);
        if(existingInvitation.isPresent()){
            throw new RuntimeException("invitation is already exists");
        }


        WorkspaceInvitation invitation = WorkspaceInvitation.builder()
                .workspace(workspace)
                .invitedUser(invitedUser)
                .invitedBy(invitedByUser)
                .status(InvitationStatus.PENDING)
                .createdAt(LocalDateTime.now())

                .build();
        workspaceInvitationRepository.save(invitation);
        // Real-time push to the invited user, if they're online
        messagingTemplate.convertAndSendToUser(
                invitedUser.getEmail(), // must match the Principal name used in your STOMP auth
                "/queue/invitations",
                new InvitationEvent(
                        "INVITE_RECEIVED",
                        invitation.getId(),
                        workspace.getWorkspace_id(),
                        workspace.getWorkspace_name(),
                        invitedByUser.getUsername()
                )
        );

        return new WorkspaceInvitationDto(
                invitation.getId(),
                invitation.getWorkspace().getWorkspace_id(),
                invitation.getWorkspace().getWorkspace_name(),
                invitation.getInvitedBy().getUsername(),
                invitation.getStatus(),
                invitation.getCreatedAt()
        );
    }
    @Transactional
    public WorkspaceInvitationDto respondToInvitation(Long invitationId, User respondingUser, boolean accept) {
        WorkspaceInvitation invitation = workspaceInvitationRepository.findByIdAndInvitedUserId(invitationId, respondingUser.getId())
                .orElseThrow(() -> new RuntimeException("Invitation not found"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new RuntimeException("Invitation already responded to");
        }

        invitation.setStatus(accept ? InvitationStatus.ACCEPTED : InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
       WorkspaceInvitation invitedUser = workspaceInvitationRepository.save(invitation);

        if(accept) {
            WorkspaceMember member = WorkspaceMember.builder()
                    .workspace(invitation.getWorkspace())
                    .user(respondingUser)
                    .role(MemberRole.MEMBER)
                    .status(WorkspaceMemberStatusType.ACTIVE)
                    .joinedAt(LocalDateTime.now())
                    .build();
            workspaceMemberRepository.save(member);
        }

        // Notify the inviter of the outcome
        messagingTemplate.convertAndSendToUser(
                invitation.getInvitedBy().getEmail(),
                "/queue/invitations",
                new InvitationEvent(
                        accept ? "INVITE_ACCEPTED" : "INVITE_REJECTED",
                        invitation.getId(),
                        invitation.getWorkspace().getWorkspace_id(),
                        invitation.getWorkspace().getWorkspace_name(),
                        invitation.getInvitedUser().getUsername()
                )
        );

        return new WorkspaceInvitationDto(
                invitedUser.getId(),
                invitedUser.getWorkspace().getWorkspace_id(),
                invitedUser.getWorkspace().getWorkspace_name(),
                invitedUser.getInvitedBy().getUsername(),
                invitedUser.getStatus(),
                invitedUser.getRespondedAt()

        );
    }

    public List<WorkspaceInvitationDto> getPendingInvitations(User user) {
        List<WorkspaceInvitation> invitations = workspaceInvitationRepository.findByInvitedUserAndStatus(user, InvitationStatus.PENDING);
        return invitations.stream()
                .map(invitation -> new WorkspaceInvitationDto(
                        invitation.getId(),
                        invitation.getWorkspace().getWorkspace_id(),
                        invitation.getWorkspace().getWorkspace_name(),
                        invitation.getInvitedBy().getUsername(),
                        invitation.getStatus(),
                        invitation.getCreatedAt()

                ))
                .toList();
    }
}
