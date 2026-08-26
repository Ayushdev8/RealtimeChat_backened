package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.Workspace;
import com.chatapplication.realtime.entity.WorkspaceMember;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember,Long> {
    Optional<WorkspaceMember> findByWorkspaceAndUser(
            Workspace workspace,
            User user
    );
    List<WorkspaceMember> findByUserAndStatusNotIn(User user,
                                                   List<WorkspaceMemberStatusType> statuses);
    List<WorkspaceMember> findByWorkspace(Workspace workspace);

}
