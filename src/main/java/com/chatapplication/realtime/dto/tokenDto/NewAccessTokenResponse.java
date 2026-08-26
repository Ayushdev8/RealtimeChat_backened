package com.chatapplication.realtime.dto.tokenDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewAccessTokenResponse {
    private String accessToken;
}
