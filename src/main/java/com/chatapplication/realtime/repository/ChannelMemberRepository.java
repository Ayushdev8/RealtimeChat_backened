package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.Channel;
import com.chatapplication.realtime.entity.ChannelMember;
import com.chatapplication.realtime.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember,Long> {
    boolean existsByChannelAndUser(Channel channel,User user);
    List<ChannelMember> findByChannel(Channel channel);
    Optional<ChannelMember> findByChannelAndUser(Channel channel,User user);
}
