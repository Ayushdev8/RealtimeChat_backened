package com.chatapplication.realtime.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workspace_id;

    private String workspace_name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "workspace",
    cascade = CascadeType.ALL,orphanRemoval = true)
    private List<WorkspaceMember> memberList =new ArrayList<>();

    @OneToMany(mappedBy = "workspace",
            cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Channel> channels = new ArrayList<>();

    @OneToMany(mappedBy = "workspace",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<WorkspaceInvitation> invitationList;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;


}
