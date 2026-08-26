package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.Workspace;
import com.chatapplication.realtime.entity.WorkspaceInvitation;
import com.chatapplication.realtime.entity.type.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, Long> {
    Optional<WorkspaceInvitation> findByIdAndInvitedUserId(
            Long id,
            Long invitedUserId
    );

    List<WorkspaceInvitation> findByInvitedUserAndStatus(
            User user,
            InvitationStatus status
    );

    Optional<WorkspaceInvitation> findByInvitedUserAndWorkspaceAndStatus(
            User user,
            Workspace workspace,
            InvitationStatus status
    );
    WorkspaceInvitation findByInvitedUserAndWorkspace(User user, Workspace workspace);




}
