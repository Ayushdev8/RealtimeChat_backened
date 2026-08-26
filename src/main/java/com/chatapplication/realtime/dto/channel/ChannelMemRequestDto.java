package com.chatapplication.realtime.dto.channel;


import com.chatapplication.realtime.entity.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelMemRequestDto {
    private String email;
    private MemberRole role;
}
