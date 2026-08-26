package com.chatapplication.realtime.dto.messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageResponseDto {
    private Long messageId;

    private Long channelId;

    private Long senderId;

    private String senderUsername;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean edited;
}
