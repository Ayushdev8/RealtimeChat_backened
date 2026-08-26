package com.chatapplication.realtime.service;

import com.chatapplication.realtime.dto.messages.MessageRequestDto;
import com.chatapplication.realtime.dto.messages.MessageResponseDto;
import com.chatapplication.realtime.entity.Channel;
import com.chatapplication.realtime.entity.ChannelMember;
import com.chatapplication.realtime.entity.Messages;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.repository.ChannelMemberRepository;
import com.chatapplication.realtime.repository.ChannelRepository;
import com.chatapplication.realtime.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public MessageResponseDto sendMessages(Long channelId, MessageRequestDto messageRequestDto, User user) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new RuntimeException("Channel Not Found")
        );
        ChannelMember channelMember = channelMemberRepository.findByChannelAndUser(channel,user).orElseThrow(
                () -> new RuntimeException("user does not member of this channel")
        );

        Messages messages = Messages.builder()
                .channel(channel)
                .sender(user)
                .content(messageRequestDto.getContent())
                .editted(false)
                .build();

        messageRepository.save(messages);
        MessageResponseDto response = new  MessageResponseDto(
                messages.getMessageId(),
                messages.getChannel().getChannelId(),
                messages.getSender().getId(),
                messages.getSender().getUsername(),
                messages.getContent(),
                messages.getCreatedAt(),
                messages.getUpdatedAt(),
                messages.isEditted()


        );
        // Broadcast to everyone in this channel
        messagingTemplate.convertAndSend(
                "/topic/channel/" + channelId,
                response
        );
        return response;


    }

    public Page<MessageResponseDto> getMessages(Long channelId, User user, Pageable pageable) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(
                () -> new RuntimeException("Channel Not Found")
        );
         channelMemberRepository.findByChannelAndUser(channel,user).orElseThrow(
                () -> new RuntimeException("you are not belong to member of this channel")
        );

        Page<Messages> messages = messageRepository.findByChannelOrderByCreatedAtDesc(channel,pageable);

        return  messages.map(message->
                     new MessageResponseDto(
                            message.getMessageId(),
                            message.getChannel().getChannelId(),
                            message.getSender().getId(),
                            message.getSender().getUsername(),
                            message.getContent(),
                            message.getCreatedAt(),
                            message.getUpdatedAt(),
                            message.isEditted()
                    )
                );

    }
}
