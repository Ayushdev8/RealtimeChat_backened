package com.chatapplication.realtime.entity;

import com.chatapplication.realtime.entity.type.ChannelType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "channels")
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long channelId;
    private String name;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspaceId")
     private Workspace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdBy")
    private User createdBy;

    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL,
    orphanRemoval = true)
    private List<ChannelMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "channel",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Messages> messages = new ArrayList<>();
}
