package com.chatapplication.realtime.dto.websocketDto;


public record PresenceEvent(
        String type,
        Long userId
) {
}
