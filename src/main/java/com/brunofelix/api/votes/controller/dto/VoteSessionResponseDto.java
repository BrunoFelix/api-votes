package com.brunofelix.api.votes.controller.dto;

import com.brunofelix.api.votes.model.Vote;
import com.brunofelix.api.votes.model.VoteSession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteSessionResponseDto {

    private Long id;

    private Long agendaId;

    private Boolean finished;

    private LocalDateTime closingAt;

    private LocalDateTime createdAt;

    private List<Vote> votes;

    public VoteSessionResponseDto (VoteSession voteSession) {
        this.id = voteSession.getId();
        this.agendaId = voteSession.getAgenda().getId();
        this.finished = voteSession.getFinished();
        this.closingAt = voteSession.getClosingAt();
        this.createdAt = voteSession.getCreatedAt();
        this.votes = voteSession.getVotes();
    }
}