package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceRepository extends JpaRepository<Workspace,Long> {
    List<Workspace> findByCreatedBy(User user);

}
