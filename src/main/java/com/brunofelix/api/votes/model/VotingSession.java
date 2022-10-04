package com.brunofelix.api.votes.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "voting_session")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VotingSession {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "closing_at")
    @NotNull
    private LocalDateTime closingAt = LocalDateTime.now().minusMinutes(1);

    @OneToOne
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    @OneToMany(mappedBy = "votingSession", cascade = CascadeType.ALL)
    private List<Vote> votes;

    @Column(name = "created_by")
    @NotNull
    private LocalDateTime createdBy = LocalDateTime.now();
}
