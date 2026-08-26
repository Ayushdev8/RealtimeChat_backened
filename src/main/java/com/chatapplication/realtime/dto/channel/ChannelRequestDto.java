package com.chatapplication.realtime.dto.channel;

import com.chatapplication.realtime.entity.type.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelRequestDto {
    private String channelName;
    private ChannelType channelType;
}
