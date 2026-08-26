package com.chatapplication.realtime.controller;

import com.chatapplication.realtime.dto.channel.ChannelMemRequestDto;
import com.chatapplication.realtime.dto.channel.ChannelMemResponseDto;
import com.chatapplication.realtime.dto.channel.ChannelRequestDto;
import com.chatapplication.realtime.dto.channel.ChannelResponseDto;
import com.chatapplication.realtime.dto.workspace.WorkspaceMemberRequestDto;
import com.chatapplication.realtime.entity.ChannelMember;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class ChannelController {
    private final ChannelService channelService;
    @PostMapping("/create/{workspaceId}")
    public ResponseEntity<ChannelResponseDto> createChannel(@PathVariable Long workspaceId, Authentication authentication, @RequestBody ChannelRequestDto requestDto){
        User user =(User) authentication.getPrincipal();
        ChannelResponseDto response = channelService.createChannel(workspaceId,user,requestDto);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/member/create/{channelId}")
    public ResponseEntity<ChannelResponseDto> addMember(@PathVariable Long channelId, @RequestBody ChannelMemRequestDto requestDto,Authentication authentication){
        User user =(User) authentication.getPrincipal();
        ChannelResponseDto responseDto = channelService.addMember(channelId,requestDto,user);


        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/getMember/{channelId}")
    public ResponseEntity<List<ChannelMemResponseDto>> getChannelMember(@PathVariable Long channelId,Authentication authentication){
        User user =(User) authentication.getPrincipal();
        List<ChannelMemResponseDto> response = channelService.getChannelMembers(channelId,user);
        return ResponseEntity.ok(response);
    }
}
