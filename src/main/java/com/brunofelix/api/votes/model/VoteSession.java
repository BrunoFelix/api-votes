package com.brunofelix.api.votes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vote_session")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSession {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "closing_at")
    @NotNull
    private LocalDateTime closingAt = LocalDateTime.now().plusMinutes(1);

    @OneToOne(optional = false)
    @JoinColumn(name = "agenda_id")
    private Agenda agenda;

    @OneToMany(mappedBy = "voteSession", fetch = FetchType.EAGER)
    private List<Vote> votes = new ArrayList<>();

    @Column(name = "created_at")
    @NotNull
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "finished")
    private Boolean finished = false;

    public VoteSession(LocalDateTime closingAt, Agenda agenda) {
        this.closingAt = closingAt;
        this.agenda = agenda;
    }

    public boolean checkVotingSessionFinished() {
        return (this.getFinished() || this.closingAt.isBefore(LocalDateTime.now()));
    }
}
