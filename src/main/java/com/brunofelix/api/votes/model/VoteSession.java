package com.brunofelix.api.votes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_session")
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class VoteSession {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "closing_at")
    @NotNull
    private LocalDateTime closingAt = LocalDateTime.now().minusMinutes(1);

    @OneToOne
    @JoinColumn(name = "agenda_id")
    @NotNull
    private Agenda agenda;

    @OneToMany(mappedBy = "voteSession", cascade = CascadeType.ALL)
    private List<Vote> votes = new ArrayList<Vote>();

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "finished")
    private Boolean finished = false;

    public VoteSession(LocalDateTime closingAt, Agenda agenda) {
        this.closingAt = closingAt;
        this.agenda = agenda;
    }
}
