package com.chatapplication.realtime.controller;

import com.chatapplication.realtime.dto.messages.MessageRequestDto;
import com.chatapplication.realtime.dto.messages.MessageResponseDto;
import com.chatapplication.realtime.entity.User;
import com.chatapplication.realtime.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/channel")
public class MessageController {
    private final MessageService messageService;

    @PostMapping("/message/{channelId}")
    public ResponseEntity<MessageResponseDto> sendMessage(
            @PathVariable Long channelId, @RequestBody MessageRequestDto messageRequestDto,
            Authentication authentication){
        User user =(User) authentication.getPrincipal();
        MessageResponseDto response = messageService.sendMessages(channelId,messageRequestDto,user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getMessage/{channelId}")
    public ResponseEntity<Page<MessageResponseDto>> getMessage(
            @PathVariable Long channelId, Pageable pageable, Authentication authentication){
        User user =(User) authentication.getPrincipal();
        Page<MessageResponseDto> response = messageService.getMessages(channelId,user,pageable);
        return ResponseEntity.ok(response);
    }
}
