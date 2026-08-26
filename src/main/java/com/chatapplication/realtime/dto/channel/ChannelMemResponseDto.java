package com.chatapplication.realtime.dto.channel;


import com.chatapplication.realtime.entity.type.MemberRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor
public class ChannelMemResponseDto {
    private Long memberId;
    private Long userId;
    private String username;
    private String email;
    private MemberRole role;
}
