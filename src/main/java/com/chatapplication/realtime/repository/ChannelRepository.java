package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.Channel;
import com.chatapplication.realtime.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel,Long> {
    boolean existsByWorkspaceAndNameIgnoreCase(Workspace workspace,String name);

    List<Channel> findByWorkspace(Workspace workspace);
}
