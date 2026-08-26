package com.chatapplication.realtime.service;

import com.chatapplication.realtime.dto.channel.ChannelMemRequestDto;
import com.chatapplication.realtime.dto.channel.ChannelMemResponseDto;
import com.chatapplication.realtime.dto.channel.ChannelRequestDto;
import com.chatapplication.realtime.dto.channel.ChannelResponseDto;
import com.chatapplication.realtime.entity.*;
import com.chatapplication.realtime.entity.type.MemberRole;
import com.chatapplication.realtime.entity.type.Roles;
import com.chatapplication.realtime.entity.type.WorkspaceMemberStatusType;
import com.chatapplication.realtime.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChannelService {
    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final ChannelRepository channelRepository;
    private final ChannelMemberRepository channelMemberRepository;
    private final UserRepository userRepository;

    public ChannelResponseDto createChannel(Long workspaceId, User user, ChannelRequestDto requestDto){
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(()->new RuntimeException("Workspace does not exist"));
        WorkspaceMember member = workspaceMemberRepository.findByWorkspaceAndUser(workspace,user).orElseThrow(()-> new RuntimeException("this user is not member of this workspace"));
        if(member.getRole() == MemberRole.MEMBER){
            throw new RuntimeException("Permission denied ");
        }
        if(channelRepository.existsByWorkspaceAndNameIgnoreCase(workspace,requestDto.getChannelName())){
            throw new RuntimeException("channel already exists in this workspace");
        }

        Channel channel = Channel.builder()
                .name(requestDto.getChannelName())
                .createdBy(user)
                .workspace(workspace)
                .type(requestDto.getChannelType())
                .build();

        ChannelMember channelMember = ChannelMember.builder()
                .channel(channel)
                .user(user)
                .status(WorkspaceMemberStatusType.ACTIVE)
                .role(MemberRole.ADMIN)
                .build();


        channelRepository.save(channel);
        channelMemberRepository.save(channelMember);
        return new ChannelResponseDto(channel.getChannelId(),channel.getName(),channel.getType());

    }

    public ChannelResponseDto addMember(Long channelId, ChannelMemRequestDto requestDto,User user){
        Channel channel =channelRepository.findById(channelId).orElseThrow(
                ()-> new RuntimeException("this channel does not exist")
        );
        User userAdd = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(()->
                new RuntimeException("User does not exist"));

        WorkspaceMember workspaceMember = workspaceMemberRepository.findByWorkspaceAndUser(channel.getWorkspace(),userAdd).orElseThrow(()->
                new RuntimeException("Entered user does not belong to this workspace "));

        WorkspaceMember workspaceMember2 = workspaceMemberRepository.findByWorkspaceAndUser(channel.getWorkspace(),user).orElseThrow(()->
                new RuntimeException("user does not belong to workspace member"));
        // to give permission so that only workspace member owner can add channel members

        if(workspaceMember2.getRole() == MemberRole.MEMBER){
            throw new RuntimeException("Workspace member are not allowed to add members in channel");
        }


        if(workspaceMember.getStatus() != WorkspaceMemberStatusType.ACTIVE){
            throw new RuntimeException("user is not an active workspace member ");
        }
        if (channelMemberRepository.existsByChannelAndUser(channel, userAdd)) {
            throw new RuntimeException("User is already a member of this channel");
        }

        ChannelMember channelMember = ChannelMember.builder()
                .channel(channel)
                .user(userAdd)
                .role(requestDto.getRole())
                .status(WorkspaceMemberStatusType.ACTIVE)
                .build();

        channelMemberRepository.save(channelMember);
        return new ChannelResponseDto(channelMember.getChannel().getChannelId(),"member is sucessfully added",channelMember.getChannel().getType());

    }

    public List<ChannelMemResponseDto> getChannelMembers(Long channelId,User user){
    Channel channel = channelRepository.findById(channelId).orElseThrow(()->
            new RuntimeException(
            "channel does not exist"
            ));
    ChannelMember channelMember = channelMemberRepository.findByChannelAndUser(channel,user).orElseThrow(
            ()-> new RuntimeException("user does not belong to this channel"));

    List<ChannelMember> members = channelMemberRepository.findByChannel(channel);

    return members.stream()
            .map(member ->{
                User memberUser = member.getUser();
                return new ChannelMemResponseDto(
                        member.getId(),
                        memberUser.getId(),
                        memberUser.getUsername(),
                        memberUser.getEmail(),
                        member.getRole()
                );
            })
            .toList();



    }
}
