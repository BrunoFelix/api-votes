package com.brunofelix.api.votes.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vote",
        uniqueConstraints = {@UniqueConstraint(name = "UK_associate_vote", columnNames = {"associate_id", "vote_session_id"})})
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "value")
    private Value value;

    @OneToOne
    private Associate associate;

    @ManyToOne
    @JoinColumn(name = "vote_session_id")
    private VoteSession voteSession;

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum Value {
        YES,
        NO
    }

    public Vote(Value value, Associate associate, VoteSession voteSession) {
        this.value = value;
        this.associate = associate;
        this.voteSession = voteSession;
    }
}
