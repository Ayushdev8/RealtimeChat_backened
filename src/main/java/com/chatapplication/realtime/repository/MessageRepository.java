package com.chatapplication.realtime.repository;

import com.chatapplication.realtime.entity.Channel;
import com.chatapplication.realtime.entity.Messages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Messages,Long> {
    Page<Messages> findByChannelOrderByCreatedAtDesc(
            Channel channel,
            Pageable pageable
    );

}
