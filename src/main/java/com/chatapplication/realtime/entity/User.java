package com.chatapplication.realtime.entity;

import com.chatapplication.realtime.entity.type.Roles;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    @Column(nullable = false,unique = true)

    private String email;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    Set<Roles> roles = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Workspace> workspaces = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<WorkspaceMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Channel> createdChannels;

    @OneToMany(mappedBy = "user")
    private List<ChannelMember> channelMembers;

    @OneToMany(mappedBy = "sender")
    private List<Messages> senderMessages;

    @OneToMany(mappedBy = "invitedUser")
    private List<WorkspaceInvitation>  invitedUsers;

    @OneToMany(mappedBy = "invitedBy")
    private List<WorkspaceInvitation> invitedUserInvites;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of();
    }
}
